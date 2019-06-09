package cyk;

import configuration.Configuration;
import configuration.ConfigurationService;
import dataset.Sequence;
import grammar.Grammar;

public class CykService {

    private static final String CALCULATE_INSIDES = "ce.calculateInsidesInCyk";

    private Configuration configuration = ConfigurationService.getConfiguration();

    private static CykService instance;

    private CykService() {
    }

    public static CykService getInstance() {
        if (instance == null)
            instance = new CykService();
        return instance;
    }

    public CykResult runCyk(Sequence testSentence, Grammar grammar, boolean enableCovering) {
        CykProcessor cykProcessor = getCyk(enableCovering);
        CykResult result = cykProcessor.runCyk(testSentence, grammar);

        if (enableCovering && !result.isParsed()) {
            return runCyk(testSentence, grammar, true);
        }
        return result;
    }

    private CykProcessor getCyk(boolean enableCovering) {
        if (configuration.getBoolean(CALCULATE_INSIDES))
            return new CykWithInsidesProcessor(enableCovering);
        return new PureCykProcessor(enableCovering);
    }

}
