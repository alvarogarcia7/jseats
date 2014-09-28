package org.jseats.jbehave;

import java.io.FileInputStream;
import java.io.FileNotFoundException;

import javax.xml.bind.JAXBException;

import static org.junit.Assert.*;

import org.jbehave.core.annotations.Given;
import org.jbehave.core.annotations.Then;
import org.jbehave.core.annotations.When;
import org.jseats.SeatAllocatorProcessor;
import org.jseats.model.Candidate;
import org.jseats.model.Result;
import org.jseats.model.SeatAllocationException;
import org.jseats.model.Tally;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class Steps {

	static Logger log = LoggerFactory.getLogger(Steps.class);

	Tally tally;
	SeatAllocatorProcessor processor = new SeatAllocatorProcessor();
	Result result;

	/*
	 * GIVEN
	 */

	@Given("empty scenario")
	public void emptyTally() {
		
		tally = null;
		result = null;
		processor.reset();
	}

	@Given("use $algorithm algorithm")
	public void setSeatAllocationAlgorithm(String algorithm)
			throws SeatAllocationException {
	
		processor.setAlgorithmByName(algorithm);
	}

	@Given("algorithm has property $key set to $value")
	public void setAlgorithmProperty(String key, String value) {
		log.debug("Setting property " + key + " to value " + value);
		processor.setProperty(key, value);
	}

	@Given("tally has candidate $candidate with $votes votes")
	public void setCandidateInTally(String candidate, int votes) {
		if (tally == null)
			tally = new Tally();

		tally.addCandidate(new Candidate(candidate, votes));
	}
	
	@Given("tally has $votes potential votes")
	public void setPotentialVotesInTally(int votes) {
		if (tally == null)
			tally = new Tally();

		tally.setPotentialVotes(votes);
	}

	/*
	 * WHEN
	 */

	@When("process with $algorithm algorithm")
	public void processWithAlgorithm(String algorithm)
			throws SeatAllocationException {

		log.debug("Processing with properties: " + processor.getProperties());

		processor.setTally(tally);
		
		setSeatAllocationAlgorithm(algorithm);
		
		result = processor.process();
	}

	@When("load $tally tally")
	public void loadTally(String tally) throws FileNotFoundException,
			JAXBException {

		this.tally = Tally.fromXML(new FileInputStream(tally));
	}

	/*
	 * THEN
	 */

	@Then("result type is $type")
	public void resultTypeIs(String type) {
		log.debug("type = " + type + " vs result.type = " + result.getType());
		assertTrue(result.getType().name().equals(type));
	}

	@Then("result candidates contain $candidate")
	public void resultCandidatesContain(String candidate)
			throws SeatAllocationException {

		assertTrue(result.containsCandidate(new Candidate(candidate)));
	}

	@Then("result candidates do not contain $candidate")
	public void resultCandidatesNotContain(String candidate)
			throws SeatAllocationException {

		assertFalse(result.containsCandidate(new Candidate(candidate)));
	}

	@Then("result single candidate is $candidate")
	public void resultSingleCandidateIs(String candidate)
			throws SeatAllocationException {

		assertTrue(result.getCandidate().getName().equals(candidate));
	}

	@Then("result single candidate isn't $candidate")
	public void resultSingleCandidateIsNot(String candidate)
			throws SeatAllocationException {

		assertFalse(result.getCandidate().getName().equals(candidate));
	}

	@Then("result is $result")
	public void resultIs(String result) throws FileNotFoundException,
			JAXBException {

		// assertFalse(this.result.equals(Result.fromXML(new FileInputStream(result))));
	}

	@Then("single result is $result")
	public void singleResultIs(String singleResult)
			throws SeatAllocationException {

		assertEquals(result.getCandidate().getName(), singleResult);
	}
}