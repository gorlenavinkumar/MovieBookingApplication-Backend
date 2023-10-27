package com.rbp.bookmymovie.security.services;

//import org.apache.kafka.clients.admin.NewTopic;
import org.bson.types.ObjectId;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
//import org.springframework.kafka.core.KafkaTemplate;

import com.rbp.bookmymovie.controller.MovieController;
import com.rbp.bookmymovie.exception.MoviesNotFound;
import com.rbp.bookmymovie.exception.SeatAlreadyBooked;
import com.rbp.bookmymovie.models.Movie;
import com.rbp.bookmymovie.models.Ticket;
import com.rbp.bookmymovie.repository.MovieRepository;
import com.rbp.bookmymovie.repository.TicketRepository;

import lombok.extern.slf4j.Slf4j;

import java.util.List;
import java.util.NoSuchElementException;

@Slf4j
@Service
public class MovieService {

    @Autowired
    private MovieRepository movieRepository;
    @Autowired
    private TicketRepository ticketRepository;
    
//    @Autowired
//	private KafkaTemplate<String, Object> kafkaTemplate;

//	@Autowired
//	private NewTopic topic;

    public List<Movie> getAllMovies() {
        return movieRepository.findAll();
    }

    public List<Movie> getMovieByName(String movieName) {
        return movieRepository.findByMovieName(movieName);
    }
    
    public List<Movie> getMovieByCompleteName(String movieName) {
        return movieRepository.searchMoivesWithCompleteName(movieName);
    }

    public List<Ticket> findSeats(String movieName, String theatreName) {
        return ticketRepository.findSeats(movieName,theatreName);
    }

    public List<Movie> findAvailableTickets(String movieName, String theatreName) {
        return movieRepository.findAvailableTickets(movieName,theatreName);
    }

    public void saveTicket(Ticket ticket) {
        ticketRepository.save(ticket);
    }

    public void saveMovie(Movie movie) {
        movieRepository.save(movie);
    }

    public List<Ticket> getAllBookedTickets(String movieName) {
        return ticketRepository.findByMovieName(movieName);
    }

    public Integer getTotalNoTickets(String movieName){
        List<Ticket> tickets = ticketRepository.findByMovieName(movieName);
        int totaltickets = 0;
        for(Ticket ticket: tickets){
            totaltickets = totaltickets + ticket.getNoOfTickets();
        }
        return totaltickets;
    }

    public List<Movie> findByMovieName(String movieName) {
        return movieRepository.findByMovieName(movieName);
    }

    public void deleteByMovieName(String movieName) {
        movieRepository.deleteByMovieName(movieName);
    }
    
    
    //NEW--------------------------------------------------------------------------
    
    public ResponseEntity<String> bookTickets(Ticket ticket, String movieName) {
		List<Ticket> allTickets = findSeats(movieName, ticket.getTheatreName());
		List<String> seatNumbers = ticket.getSeatNumber();
		int numTickets = ticket.getNoOfTickets();
		if (seatNumbers.size() < numTickets) {
			throw new IllegalArgumentException("Not enough seat numbers provided");
		}

		for (Ticket each : allTickets) {
			for (int i = 0; i < numTickets; i++) {
				if (each.getSeatNumber().contains(seatNumbers.get(i))) {
					throw new SeatAlreadyBooked("Seat number " + seatNumbers.get(i) + " is already booked");
				}
			}
		}
		// logger.info(movieName + " " + ticket.getTheaterName());
		// Movie availableTickets=findMovieByMovieNameAndTheatreName(movieName,
		// ticket.getMovieName());

		// logger.info(findAvailableTickets(movieName,
		// ticket.getTheaterName()).toString());
		int availableTickets = findAvailableTickets(movieName, ticket.getTheatreName()).get(0)
				.getNoOfTicketsAvailable();
//		System.out.println(availableTickets);

		if (availableTickets >= ticket.getNoOfTickets()) {

			saveTicket(ticket);
//			kafkaTemplate.send(topic.name(), "Movie ticket booked. " + "Booking Details are: " + ticket);
			Movie movies = findMovieByMovieNameAndTheatreName(movieName, ticket.getTheatreName());
			
			movies.setNoOfTicketsAvailable(movies.getNoOfTicketsAvailable() - ticket.getNoOfTickets());
			movies.setTicketsStatus(updateTicketStatus(movies));
			saveMovie(movies);
			return new ResponseEntity<>("Ticket Booked Successfully with seat number " + ticket.getSeatNumber(),
					HttpStatus.OK);
		} else {
			return new ResponseEntity<>("All Tickets Sold out", HttpStatus.OK);
		}

	}
    
    
    
    public String updateTicketStatus(Movie movie) {

		if (movie == null) {
			throw new MoviesNotFound("Movie not found: " + movie.getMovieName());
		}

		List<Ticket> ticketList = ticketRepository.findByMovieNameAndTheaterName(movie.getMovieName(), movie.getTheatreName());
		int ticketsBooked = 0;
		for (Ticket ticket : ticketList) {
			ticketsBooked = ticketsBooked + ticket.getNoOfTickets();
		}

		if (movie.getNoOfTicketsAvailable() == 0) {
			return "SOLD OUT";
		} else if (ticketsBooked >= movie.getNoOfTicketsAvailable()) {
			return "BOOK ASAP";
		} else {
			return "AVAILABLE";
		}

	}
    
    
    public Movie findMovieByMovieNameAndTheatreName(String movieName, String theatername) {
		return movieRepository.findByMovieNameAndTheaterName(movieName, theatername);
	}
    
    
    public String updateTicketStatus(String movieName, ObjectId ticket) {
		List<Movie> movie = movieRepository.findByMovieName(movieName);
		List<Ticket> tickets = ticketRepository.findBy_id(ticket);

		if (movie == null) {
			throw new MoviesNotFound("Movie not found: " + movieName);
		}
		if (tickets == null) {
			throw new NoSuchElementException("Ticket Not Found: " + ticket);
		}

		int ticketsBooked = getTotalNoTickets(movieName);

		for (Movie movies : movie) {
			// log.info("No. of Tickets available- " +movies.getNoOfTicketsAvailable());
			if (movies.getNoOfTicketsAvailable() == 0) {
				movies.setTicketsStatus("SOLD OUT");
			} else if (ticketsBooked >= movies.getNoOfTicketsAvailable()) {
				movies.setTicketsStatus("BOOK ASAP");
			} else {
				movies.setTicketsStatus("AVAILABLE");
			}
			saveMovie(movies);
		}

//		for (Movie movies : movie) {
//			if (ticketsBooked >= movies.getNoOfTicketsAvailable()) {
//				movies.setTicketStatus("SOLD OUT");
//			} else {
//				movies.setTicketStatus("BOOK AS SOON AS POSSIBLE");
//			}
//
//			saveMovie(movies);
//		}

		// kafka impl
//		 kafkaTemplate.send(topic.name(), "tickets status upadated by the Admin for movie "+ movieName);
		return "Ticket status updated successfully";
	}

    
}
