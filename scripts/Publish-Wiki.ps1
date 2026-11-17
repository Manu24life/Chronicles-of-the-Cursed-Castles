[CmdletBinding()]
param(
    [switch]$Publish,
    [ValidateNotNullOrEmpty()]
    [string]$WikiUrl = 'https://github.com/Manu24life/Chronicles-of-the-Cursed-Castles.wiki.git'
)

$ErrorActionPreference = 'Stop'

function Invoke-WikiGit {
    param([string[]]$GitArguments)

    & git @GitArguments
    if ($LASTEXITCODE -ne 0) {
        throw "Git non ha completato l'operazione (codice $LASTEXITCODE)."
    }
}

Get-Command git -ErrorAction Stop | Out-Null
$projectRoot = Split-Path -Parent $PSScriptRoot
$sourceDirectory = Join-Path $projectRoot 'docs/wiki'
$pages = @(Get-ChildItem -LiteralPath $sourceDirectory -Filter '*.md' -File)
if ($pages.Count -eq 0 -or -not (Test-Path -LiteralPath (Join-Path $sourceDirectory 'Home.md'))) {
    throw 'La cartella docs/wiki deve contenere Home.md e le pagine da pubblicare.'
}

$workDirectory = Join-Path $projectRoot ('build/wiki-publish-' + [guid]::NewGuid().ToString('N'))
New-Item -ItemType Directory -Path (Split-Path -Parent $workDirectory) -Force | Out-Null

try {
    Invoke-WikiGit -GitArguments @('clone', '--', $WikiUrl, $workDirectory)
} catch {
    throw "Impossibile clonare la wiki. Crea prima la pagina Home su GitHub e verifica l'accesso Git. Dettagli: $($_.Exception.Message)"
}

foreach ($page in $pages) {
    Copy-Item -LiteralPath $page.FullName -Destination (Join-Path $workDirectory $page.Name)
}

$pageNames = @($pages | ForEach-Object { $_.Name })
Invoke-WikiGit -GitArguments (@('-C', $workDirectory, 'add', '--') + $pageNames)
Invoke-WikiGit -GitArguments @('-C', $workDirectory, 'diff', '--cached', '--stat')
Write-Host "Copia locale della wiki: $workDirectory"

& git -C $workDirectory diff --cached --quiet
$differenceStatus = $LASTEXITCODE
if ($differenceStatus -eq 0) {
    Write-Host 'La wiki e gia aggiornata; nessuna modifica da pubblicare.'
    return
}
if ($differenceStatus -ne 1) {
    throw "Impossibile verificare le modifiche (codice $differenceStatus)."
}
if (-not $Publish) {
    Write-Host 'Anteprima pronta. Per pubblicare esegui nuovamente lo script con -Publish.'
    return
}

$wikiBranch = & git -C $workDirectory symbolic-ref --short HEAD
if ($LASTEXITCODE -ne 0 -or [string]::IsNullOrWhiteSpace($wikiBranch)) {
    throw 'Impossibile individuare il branch della wiki.'
}

Invoke-WikiGit -GitArguments @('-C', $workDirectory, 'commit', '-m', 'Documenta Chronicles of the Cursed Castles')
Invoke-WikiGit -GitArguments @('-C', $workDirectory, 'push', 'origin', "HEAD:refs/heads/$wikiBranch")
Write-Host "Wiki pubblicata su $WikiUrl"
