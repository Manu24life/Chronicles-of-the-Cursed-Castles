package it.unicam.cs.mpgc.rpg125657.cursedcastles.model.combattimento;

import it.unicam.cs.mpgc.rpg125657.cursedcastles.model.eroe.Eroe;

import java.util.Objects;

/** Cura una percentuale della vita massima per un numero limitato di utilizzi. */
public final class CuraPercentualeLimitata implements RegolaCura {

    public static final int PERCENTUALE_STANDARD = 5;
    public static final int UTILIZZI_STANDARD = 2;

    private final int percentuale;
    private final int utilizziMassimi;
    private int utilizziEffettuati;

    public CuraPercentualeLimitata() {
        this(PERCENTUALE_STANDARD, UTILIZZI_STANDARD);
    }

    public CuraPercentualeLimitata(int percentuale, int utilizziMassimi) {
        if (percentuale <= 0 || percentuale > 100) {
            throw new IllegalArgumentException("La percentuale deve essere compresa tra 1 e 100");
        }
        if (utilizziMassimi < 1) {
            throw new IllegalArgumentException("Gli utilizzi massimi devono essere positivi");
        }
        this.percentuale = percentuale;
        this.utilizziMassimi = utilizziMassimi;
    }

    @Override
    public RisultatoAzione usa(Eroe eroe) {
        Objects.requireNonNull(eroe, "L'eroe non può essere null");
        if (!eroe.isVivo()) {
            return RisultatoAzione.fallimento(
                    EsitoAzione.AZIONE_NON_CONSENTITA,
                    "Un eroe sconfitto non può curarsi"
            );
        }
        if (utilizziRimanenti() == 0) {
            return RisultatoAzione.fallimento(
                    EsitoAzione.CURE_ESAURITE,
                    "Le cure disponibili per questo castello sono esaurite"
            );
        }
        if (eroe.puntiVita() == eroe.statisticheBase().puntiVitaMassimi()) {
            return RisultatoAzione.fallimento(
                    EsitoAzione.VITA_GIA_PIENA,
                    "L'eroe ha già tutti i punti vita"
            );
        }

        int curaRichiesta = Math.max(
                1,
                eroe.statisticheBase().puntiVitaMassimi() * percentuale / 100
        );
        int vitaPrima = eroe.puntiVita();
        eroe.recuperaPuntiVita(curaRichiesta);
        int curaEffettiva = eroe.puntiVita() - vitaPrima;
        utilizziEffettuati++;

        return RisultatoAzione.successo(
                0,
                curaEffettiva,
                eroe.nome() + " recupera " + curaEffettiva + " punti vita"
        );
    }

    @Override
    public int utilizziRimanenti() {
        return utilizziMassimi - utilizziEffettuati;
    }
}
