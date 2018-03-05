package at.acam.planomat;

import java.util.ArrayList;
import java.util.List;

import org.joda.time.Duration;
import org.joda.time.format.DateTimeFormat;
import org.joda.time.format.DateTimeFormatter;
import org.optaplanner.core.api.solver.Solver;
import org.optaplanner.core.api.solver.SolverFactory;
import org.optaplanner.core.impl.score.director.ScoreDirector;
import org.optaplanner.core.impl.score.director.ScoreDirectorFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import at.acam.planomat.domain.AirportSolution;
import at.acam.planomat.domain.Plane;

public class Starter
{
	private static Logger logger = LoggerFactory.getLogger(Starter.class);
	public static final String SOLVER_CONFIG_XML = "PlanomatSolverConfig.xml";
	public static ScoreDirector scoreDirector;

	public static DateTimeFormatter fmt = DateTimeFormat.forPattern("yyyy/MM/dd HH:mm");
	
	public static void main(String[] args)
	{
		// see: https://whimsycode.wordpress.com/2013/02/23/drools-planner-introduction-by-example/
		
		Solver solver = SolverFactory.createFromXmlResource(SOLVER_CONFIG_XML).buildSolver();
		ScoreDirectorFactory scoreDirectorFactory = solver.getScoreDirectorFactory();
		scoreDirector = scoreDirectorFactory.buildScoreDirector();
		
		AirportSolution as = Starter.generateProblem();

		scoreDirector.setWorkingSolution(as);
		logger.info("TRYING TO FIND A SOLUTION");
		
		solver.solve(as);
		AirportSolution bestSolution = (AirportSolution) solver.getBestSolution();
		logger.info("\n" + bestSolution.toString());
	}

	/**
	 * Statically generate a starting situation. Here, the planning value (the
	 * TSATs) could be anything - but if you have a fast way of initialising the
	 * values to something reasonable, it will help planner as the path to the
	 * optimal solution will be shorter.
	 * 
	 * At the moment TSAT is initialised to be equal to the TOBT - could you
	 * improve this programatically? Hint: Are most flights delayed? if so, by
	 * how much on average?
	 * 
	 * @return
	 * @throws ParseException
	 */
	public static AirportSolution generateProblem() 
	{

		final long MINUTE = 6000;
		List<Plane> p = new ArrayList<Plane>();

		p.add(new Plane("a", fmt.parseDateTime("2013/01/01 01:00").toInstant(), new Duration(10 * MINUTE)));
		p.add(new Plane("b", fmt.parseDateTime("2013/01/01 01:01").toInstant(), new Duration(12 * MINUTE)));
		p.add(new Plane("c", fmt.parseDateTime("2013/01/01 01:10").toInstant(), new Duration(2  * MINUTE)));
		p.add(new Plane("d", fmt.parseDateTime("2013/01/01 01:20").toInstant(), new Duration(13 * MINUTE)));
		p.add(new Plane("e", fmt.parseDateTime("2013/01/01 01:30").toInstant(), new Duration(3  * MINUTE)));
		p.add(new Plane("f", fmt.parseDateTime("2013/01/01 01:30").toInstant(), new Duration(5  * MINUTE)));
		p.add(new Plane("g", fmt.parseDateTime("2013/01/01 01:30").toInstant(), new Duration(6  * MINUTE)));
		p.add(new Plane("h", fmt.parseDateTime("2013/01/01 01:30").toInstant(), new Duration(10 * MINUTE)));
		p.add(new Plane("i", fmt.parseDateTime("2013/01/01 01:30").toInstant(), new Duration(7  * MINUTE)));
		p.add(new Plane("j", fmt.parseDateTime("2013/01/01 01:30").toInstant(), new Duration(4  * MINUTE)));

		AirportSolution a = new AirportSolution(p);

		return a;
	}
}
