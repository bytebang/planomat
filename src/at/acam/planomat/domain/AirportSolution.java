package at.acam.planomat.domain;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import org.optaplanner.core.api.domain.solution.PlanningEntityCollectionProperty;
import org.optaplanner.core.api.domain.solution.PlanningSolution;
import org.optaplanner.core.api.domain.solution.Solution;
import org.optaplanner.core.api.domain.valuerange.ValueRangeProvider;
import org.optaplanner.core.api.score.buildin.hardsoft.HardSoftScore;
import org.joda.time.Duration;
import org.joda.time.Instant;

/**
 * AirportSolution represents a single possible solution to the planning
 * problem.
 */
@PlanningSolution
public class AirportSolution implements Solution<HardSoftScore>
{

	private HardSoftScore score;
	private List<Plane> planPlanes = new ArrayList<Plane>();

	public AirportSolution()
	{

	}

	/**
	 * List of all planes to plan for
	 * 
	 * @param planPlanes
	 *            - planes that need TSATs calculating
	 */
	public AirportSolution(List<Plane> planPlanes)
	{
		this.planPlanes.addAll(planPlanes);
	}

	/**
	 * This annotation tells drools that this property contains the planning
	 * elements.
	 * 
	 * @return the planes at this airport
	 */
	@PlanningEntityCollectionProperty
	public List<Plane> getPlanPlanes()
	{
		return planPlanes;
	}

	public void setPlanPlanes(List<Plane> planes)
	{
		this.planPlanes = planes;
	}

	@Override
	public HardSoftScore getScore()
	{
		return score;
	}

	@Override
	public void setScore(HardSoftScore score)
	{
		this.score = score;
	}

	/**
	 * The possible solutions come from here. Currently we are saying that every
	 * minute from the first planes TOBT to the last planes TOBT (plus one
	 * hour).
	 * 
	 * By reducing count of values here you will reduce the possible number of
	 * solutions. If you have a way of identifying bad values (for example you
	 * know that +1 hour is too much).
	 * 
	 * Be careful though - Planner can only work with what you give it here, be
	 * too strict and you may be preventing planner for coming up with the best
	 * solution.
	 * 
	 * @return the full list of possible TSATs that planner can try
	 */
	@ValueRangeProvider(id = "tsatOptions")
	public List<Instant> getTsatOptions()
	{
		Instant earliest = planPlanes.get(0).getTobt();
		Instant latest = planPlanes.get(0).getTobt();
		List<Instant> result = new ArrayList<Instant>();

		for (Plane p : planPlanes)
		{
			if (p.getTobt().isBefore(earliest))
			{
				earliest = p.getTobt();
			}

			if (p.getTobt().isAfter(latest))
			{
				latest = p.getTobt();
			}
		}

		Instant candidate = earliest;
		result.add(candidate); // add first time as a candidate

		// add each minute from earliest to latest (should be latest hour)
		while (candidate.isBefore(latest.plus(new Duration(1000 * 60 * 60))))
		{
			// increment by one minute
			candidate = candidate.plus(new Duration(1000 * 60));
			result.add(candidate);
		}

		return result;

	}

	@SuppressWarnings({ "rawtypes", "unchecked" })
	@Override
	public Collection<? extends Object> getProblemFacts()
	{
		/*
		 * Currently not implementing facts, but you can pass static facts into
		 * the Drools environment here if you like - things like stand positions
		 * and taxi times for particular types of planes.
		 */
		return new ArrayList();
	}

	/**
	 * Deep clone this solution. Clones and hash codes must be done correctly if
	 * you want to avoid problems being reported from inside Drools (trust me,
	 * sometimes they are really hard to trace).
	 */
	public Solution<HardSoftScore> cloneSolution()
	{

		List<Plane> clonePlanes = new ArrayList<Plane>();

		for (Plane p : planPlanes)
		{
			clonePlanes.add(p.clonePlane());
		}

		AirportSolution clone = new AirportSolution(clonePlanes);

		clone.setScore(this.getScore());

		return clone;
	}

	@Override
	public String toString()
	{
		String returnValue = "";

		returnValue += "ID \t TOBT \t TSAT \t Interval \t Delay \n";
		for (Plane p : planPlanes)
		{
			Long duration = new Duration(p.getTobt(), p.getTsat()).getStandardSeconds();
			returnValue += p.getId() + "\t" + p.getTobt() + "\t" + p.getTsat()	+ "\t" + p.getTaxiInterval() + "\t" + duration + "\t\n";
		}
		
		returnValue = returnValue.replace("2013-01-01T00:", "").replace(".000Z", "");
		
		return returnValue;
	}

}
