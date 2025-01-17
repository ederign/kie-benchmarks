package org.jboss.qa.brms.performance.examples.travelingtournament;

import java.io.File;

import org.jboss.qa.brms.performance.examples.AbstractExample;
import org.jboss.qa.brms.performance.examples.Examples;
import org.jboss.qa.brms.performance.examples.travelingtournament.persistance.TravelingTournamentDao;
import org.optaplanner.core.api.solver.Solver;
import org.optaplanner.core.api.solver.SolverFactory;
import org.optaplanner.core.config.score.director.ScoreDirectorFactoryConfig;
import org.optaplanner.core.config.solver.EnvironmentMode;
import org.optaplanner.core.config.solver.SolverConfig;
import org.optaplanner.core.config.solver.termination.TerminationConfig;
import org.optaplanner.examples.travelingtournament.domain.Match;
import org.optaplanner.examples.travelingtournament.domain.TravelingTournament;
import org.optaplanner.examples.travelingtournament.score.TravelingTournamentConstraintProvider;

public final class TravelingTournamentExample extends AbstractExample<TravelingTournament> {

    private static final String SOLVER_CONFIG =
            "org/jboss/qa/brms/performance/examples/travelingtournament/solver/travelingTournamentSolverConfig.xml";
    private final TravelingTournamentDao travelingTournamentDao = new TravelingTournamentDao();

    @Override
    public TravelingTournament loadSolvingProblem(File file) {
         return travelingTournamentDao.readSolution(file);
    }

    public TravelingTournament loadSolvingProblem(TravelingTournamentExample.DataSet dataset) {
        return loadSolvingProblem(new File(travelingTournamentDao.getDataDir(), dataset.getFilename()));
    }

    public TravelingTournament createInitialSolution(TravelingTournamentExample.DataSet dataSet) {
        SolverConfig solverConfig = Examples.TRAVELING_TOURNAMENT.getBaseSolverConfig();

        SolverFactory<TravelingTournament> solverFactory = SolverFactory.create(solverConfig);
        Solver<TravelingTournament> constructionSolver = solverFactory.buildSolver();

        TravelingTournament solution = Examples.TRAVELING_TOURNAMENT.loadSolvingProblem(dataSet);
        return constructionSolver.solve(solution);
    }

    @Override
    public SolverConfig getBaseSolverConfig() {
        return new SolverConfig()
                .withSolutionClass(TravelingTournament.class)
                .withEnvironmentMode(EnvironmentMode.REPRODUCIBLE)
                .withEntityClasses(Match.class)
                .withTerminationConfig(new TerminationConfig().withScoreCalculationCountLimit(10_000L))
                .withScoreDirectorFactory(new ScoreDirectorFactoryConfig()
                                                  .withConstraintProviderClass(TravelingTournamentConstraintProvider.class)
                                                  .withInitializingScoreTrend("ONLY_DOWN"));
    }

    @Override
    protected String getSolverConfigResource() {
        return SOLVER_CONFIG;
    }

    public enum DataSet {
        SUPER_06("4-super06.json"),
        SUPER_10("4-super10.json"),
        SUPER_14("4-super14.json");

        DataSet(String file) {
            this.filename = file;
        }

        private String filename;

        public String getFilename() {
            return filename;
        }
    }
}
