# Grafica delle battaglie

Asset raster originali generati con lo strumento integrato image_gen per questo progetto.


- `nemici/scheletro.png`: guardia scheletrica con spada consumata.
- `nemici/orco.png`: carceriere con corazza corrosa e mannaia.
- `nemici/vampiro.png`: nobile cadaverico con mantello lacero.
- `nemici/drago.png`: drago maledetto accovacciato, ali parzialmente raccolte.
- `scenari/ossa.png`: ossario e cripte del Castello delle Ossa.
- `scenari/ferro.png`: prigione e saracinesca della Fortezza di Ferro.
- `scenari/maledizione.png`: cappella gotica in rovina della Rocca della Maledizione.

I master selezionati sono conservati integralmente. Gli sprite hanno canale alpha;
nessun colore viene rimosso a runtime. Il caricatore limita la decodifica a 640 px di larghezza
per i nemici e 1920 px per i panorami, con cache condivisa e proporzioni preservate.
Ogni visualizzazione usa un proprio nodo Texture di FXGL Core 25.0.1.

Queste immagini non sono sprite sheet:
i feedback di danno e sconfitta usano le animazioni di trasformazione e opacità FXGL.
