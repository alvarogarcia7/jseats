package org.jseats.model.methods;

import java.util.Properties;

import org.jseats.model.InmutableTally;
import org.jseats.model.Result;
import org.jseats.model.SeatAllocationException;
import org.jseats.model.Result.ResultType;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public abstract class LargestRemainderMethod extends SeatAllocationMethod {

	static Logger log = LoggerFactory.getLogger(LargestRemainderMethod.class);

	@Override
	public Result process(InmutableTally tally, Properties properties)
			throws SeatAllocationException {
		
		int numberOfCandidates = tally.getNumberOfCandidates();
		int numberOfSeats = Integer.parseInt(properties.getProperty("numberOfSeats"));
		int numberOfUnallocatedSeats = numberOfSeats;
		
		int[] seatsPerCandidate = new int[numberOfCandidates];
		int[] remainderVotesPerCandidate = new int[numberOfCandidates];
		
		// Get the quotient (decimals dropped)
		int quotient = (int) quotient(tally.getEffectiveVotes(),numberOfSeats);
		
		log.debug("quotient is: " + quotient);
		
		// Let's assign direct seats to candidates
		// That is, $quotient votes = 1 seat  
		for(int i = 0; i < numberOfCandidates; i++) {
			seatsPerCandidate[i] = tally.getCandidateAt(i).getVotes() / quotient;
			remainderVotesPerCandidate[i] = tally.getCandidateAt(i).getVotes() - (seatsPerCandidate[i] * quotient);
			numberOfUnallocatedSeats -= seatsPerCandidate[i];
		}
		
		traceIntermediateState(numberOfCandidates, 
				seatsPerCandidate, 
				remainderVotesPerCandidate, 
				numberOfUnallocatedSeats);
	
		// Largest Remainder
		// Let's assign unallocated seats to candidates below the quotient, 
		// from more voted to less voted until no more unallocated seats remain.
		while(numberOfUnallocatedSeats > 0) {
			
			int maxIndex = -1;
			int maxVotes = -1;
			
			for(int i = 0; i < numberOfCandidates; i++) {
				if(remainderVotesPerCandidate[i] > maxVotes) {
					maxIndex = i;
					maxVotes = remainderVotesPerCandidate[i];
				}
			}
			
			seatsPerCandidate[maxIndex]++;
			remainderVotesPerCandidate[maxIndex] = -2;
			numberOfUnallocatedSeats--;
		}
		
		traceIntermediateState(numberOfCandidates, 
				seatsPerCandidate, 
				remainderVotesPerCandidate, 
				numberOfUnallocatedSeats);
		
		// Time to spread allocated seats to results
		Result result = new Result(ResultType.MULTIPLE);
		
		for(int candidate = 0; candidate < numberOfCandidates; candidate++) {
			for(int seat = 0; seat < seatsPerCandidate[candidate]; seat++) {
				result.addSeat( tally.getCandidateAt(candidate) );
			}
		}
		
		for(int i = 0; i < result.getNumerOfSeats(); i++) {
			log.debug("seat #" + i + ":" + result.getSeatAt(i));
		}
		
		return result;
	}
	
	private void traceIntermediateState(int numberOfCandidates, int[] seatsPerCandidate, int[] remainderVotesPerCandidate, int numberOfUnallocatedSeats) {
		for(int i = 0; i < numberOfCandidates; i++) {
			log.trace("seatsPerQuotient["+i+"]: " + seatsPerCandidate[i]);
			log.trace("votesPerRemainder["+i+"]: " + remainderVotesPerCandidate[i]);
		}
		
		log.trace("numberOfUnallocatedSeats: "+numberOfUnallocatedSeats);
	}

	public abstract double quotient(int numberOfVotes, int numberOfSeats);
}