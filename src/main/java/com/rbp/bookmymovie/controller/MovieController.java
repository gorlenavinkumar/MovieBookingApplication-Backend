package com.rbp.bookmymovie.controller;

import io.swagger.v3.oas.annotations.OpenAPIDefinition;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.info.Info;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import lombok.extern.slf4j.Slf4j;
//import org.apache.kafka.clients.admin.NewTopic;
import org.bson.types.ObjectId;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
//import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;

import com.rbp.bookmymovie.exception.MoviesNotFound;
import com.rbp.bookmymovie.exception.SeatAlreadyBooked;
import com.rbp.bookmymovie.models.ERole;
import com.rbp.bookmymovie.models.Movie;
import com.rbp.bookmymovie.models.Role;
import com.rbp.bookmymovie.models.Ticket;
import com.rbp.bookmymovie.models.User;
import com.rbp.bookmymovie.payload.request.LoginRequest;
import com.rbp.bookmymovie.repository.MovieRepository;
import com.rbp.bookmymovie.repository.RoleRepository;
import com.rbp.bookmymovie.repository.TicketRepository;
import com.rbp.bookmymovie.repository.UserRepository;
import com.rbp.bookmymovie.security.services.MovieService;
import com.rbp.bookmymovie.security.services.UserDetailsImpl;
import com.rbp.bookmymovie.security.services.UserDetailsServiceImpl;

import java.util.List;
import java.util.NoSuchElementException;
import java.util.Optional;
import java.util.*;

@CrossOrigin(origins = {"http://localhost:3000", "http://localhost:3001", "http://localhost:3002"})
@RestController
@RequestMapping("/api/v1.0/moviebooking")
@OpenAPIDefinition(
        info = @Info(
                title = "BookMyMovie Application API",
                description = "This API provides endpoints for booking tickets for movies and managing them."
        )
)
@Slf4j
public class MovieController {
    @Autowired
    private UserRepository userRepository;

    @Autowired
    PasswordEncoder passwordEncoder;
    @Autowired
    private MovieService movieService;
    @Autowired
    private MovieRepository movieRepository;

    @Autowired
    private TicketRepository ticketRepository;

    @Autowired
    private UserDetailsServiceImpl userDetailsService;

//    @Autowired
////    private KafkaTemplate<String, Object> kafkaTemplate;
//
//    @Autowired
//    private NewTopic topic;


    @PutMapping("/{loginId}/forgot")
//    @SecurityRequirement(name = "Bearer Authentication")
    @Operation(summary = "Reset Password")
//    @PreAuthorize("hasRole('USER') or hasRole('ADMIN')")
    public ResponseEntity<String> changePassword(@RequestBody LoginRequest loginRequest, @PathVariable String loginId){
        log.debug("forgot password endopoint accessed by "+loginRequest.getLoginId());
        Optional<User> user1 = userRepository.findByLoginId(loginId);
            User availableUser = user1.get();
            User updatedUser = new User(
                            loginId,
                    availableUser.getFirstName(),
                    availableUser.getLastName(),
                    availableUser.getEmail(),
                    availableUser.getContactNumber(),
                    passwordEncoder.encode(loginRequest.getPassword())
                    );
            updatedUser.set_id(availableUser.get_id());
            updatedUser.setRoles(availableUser.getRoles());
            userRepository.save(updatedUser);
            log.debug(loginRequest.getLoginId()+" has password changed successfully");
            return new ResponseEntity<>("Users password changed successfully",HttpStatus.OK);
    }

    @GetMapping("/all")
    @SecurityRequirement(name = "Bearer Authentication")
    @Operation(summary = "Search All Movies")
//    @PreAuthorize("hasRole('USER')or hasRole('ADMIN')")
    public ResponseEntity<List<Movie>> getAllMovies(){
        log.debug("Here we can access all the Available Movies");
        List<Movie> movieList = movieService.getAllMovies();
        if(movieList.isEmpty()){
            log.debug("Currently no movies are available");
            throw new MoviesNotFound("No Movies are available");
        }
        else{
            log.debug("Listed all the Available Movies");
            return new ResponseEntity<>(movieList, HttpStatus.OK);
        }
    }

    
    
    
    @GetMapping("/movies/search/{movieName}")
    @SecurityRequirement(name = "Bearer Authentication")
    @Operation(summary = "Search Movies by Movie Name")
//    @PreAuthorize("hasRole('USER')")
    public ResponseEntity<List<Movie>> getMovieByName(@PathVariable String movieName){
        log.debug("here search a movie by its name");
        List<Movie> movieList = movieService.getMovieByName(movieName);
        if(movieList.isEmpty()){
            log.debug("currently no movies are available");
            throw new MoviesNotFound("Movies Not Found");
        }
        else
            log.debug("listed the available movies with title:"+movieName);
            return new ResponseEntity<>(movieList,HttpStatus.OK);
    }
    
    
    
    @GetMapping("/movies/searchwithcompletename/{movieName}")
    @SecurityRequirement(name = "Bearer Authentication")
    @Operation(summary = "Search Movies by Movie Name")
//    @PreAuthorize("hasRole('USER')")
    public ResponseEntity<List<Movie>> getMovieByCompleteName(@PathVariable String movieName){
        log.debug("here search a movie by its name");
        List<Movie> movieList = movieService.getMovieByCompleteName(movieName);
        if(movieList.isEmpty()){
            log.debug("currently no movies are available");
            throw new MoviesNotFound("Movies Not Found");
        }
        else
            log.debug("listed the available movies with title:"+movieName);
            return new ResponseEntity<>(movieList,HttpStatus.OK);
    }
    
    
    
    

//    @PostMapping("/{movieName}/add")@SecurityRequirement(name = "Bearer Authentication")
//    @Operation(summary = "book ticket")
//    @PreAuthorize("hasRole('USER')")
//    public ResponseEntity<String> bookTickets(@RequestBody Ticket ticket, @PathVariable String movieName) {
//        log.debug(ticket.getLoginId()+" entered to book tickets");
//        List<Ticket> allTickets = movieService.findSeats(movieName,ticket.getTheatreName());
//        for(Ticket each : allTickets){
//            for(int i = 0; i < ticket.getNoOfTickets(); i++){
//                if(each.getSeatNumber().contains(ticket.getSeatNumber().get(i))){
//                    log.debug("seat is already booked");
//                    throw new SeatAlreadyBooked("Seat number "+ticket.getSeatNumber().get(i)+" is already booked");
//                }
//            }
//        }
//
//        if(movieService.findAvailableTickets(movieName,ticket.getTheatreName()).get(0).getNoOfTicketsAvailable() >=
//                ticket.getNoOfTickets()){
//
//            log.info("available tickets "
//                    +movieService.findAvailableTickets(movieName,ticket.getTheatreName()).get(0).getNoOfTicketsAvailable());
//            movieService.saveTicket(ticket);
//            log.debug(ticket.getLoginId()+" booked "+ticket.getNoOfTickets()+" tickets");
////            kafkaTemplate.send(topic.name(),"Movie ticket booked. " +
////                    "Booking Details are: "+
////            ticket);
//            Movie movie = movieRepository.findByMovieNameAndTheaterName(movieName, ticket.getTheatreName());
//            int available_tickets = 0;
////            for (Movie movie : movies) {
//                available_tickets = movie.getNoOfTicketsAvailable() - ticket.getNoOfTickets();
//                movie.setNoOfTicketsAvailable(available_tickets);
//                movieService.saveMovie(movie);
////            }
//            updateTicketStatus(movieName);
//            return new ResponseEntity<>("Tickets Booked Successfully with seat numbers"+ticket.getSeatNumber(),HttpStatus.OK);
//        }
//        else{
//            log.debug("tickets sold out");
//            return new ResponseEntity<>("\"All tickets sold out\"",HttpStatus.OK);
//        }
//    }
    
    
    
  @PostMapping("/{movieName}/add")@SecurityRequirement(name = "Bearer Authentication")
  @Operation(summary = "book ticket")
  @PreAuthorize("hasRole('USER')")
  public ResponseEntity<String> bookTickets(@RequestBody Ticket ticket, @PathVariable String movieName) {
	  List<Ticket> allTickets = movieService.findSeats(movieName, ticket.getTheatreName());
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
		int availableTickets = movieService.findAvailableTickets(movieName, ticket.getTheatreName()).get(0)
				.getNoOfTicketsAvailable();
//		System.out.println(availableTickets);

		if (availableTickets >= ticket.getNoOfTickets()) {

			movieService.saveTicket(ticket);
//			kafkaTemplate.send(topic.name(), "Movie ticket booked. " + "Booking Details are: " + ticket);
			Movie movies = movieService.findMovieByMovieNameAndTheatreName(movieName, ticket.getTheatreName());
			
			movies.setNoOfTicketsAvailable(movies.getNoOfTicketsAvailable() - ticket.getNoOfTickets());
			movies.setTicketsStatus(updateTicketStatus(movies));
			movieService.saveMovie(movies);
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

    
    
    
    
//    @PostMapping("/{movieName}/add")
//	@Operation(summary="Book tickets for a movie")
//	public ResponseEntity<String> bookTickets(@RequestBody Ticket ticket,@PathVariable("movieName") String movieName){
//		
//		return movieService.bookTickets(ticket,movieName);
//	}
    
    
    

    @GetMapping("/getallbookedtickets/{movieName}")@SecurityRequirement(name = "Bearer Authentication")
    @Operation(summary = "get all booked tickets(Admin Only)")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<List<Ticket>> getAllBookedTickets(@PathVariable String movieName){
        return new ResponseEntity<>(movieService.getAllBookedTickets(movieName),HttpStatus.OK);
    }


    
    
    
//	@PutMapping("/{movieName}/update")
//	@PreAuthorize("hasRole('ADMIN')")
//	public ResponseEntity<String> updateTicketStatus(@PathVariable String movieName) {
//	    List<Movie> movie = movieRepository.findByMovieName(movieName);
//	    if (movie == null) {
//	        throw new MoviesNotFound("Movie not found: " + movieName);
//	    }
//	
//	    int ticketsBooked = movieService.getTotalNoTickets(movieName);
//	    log.info("Total Tickets booked- " +ticketsBooked);
//	    for (Movie movies : movie) {
//	    	log.info("No. of Tickets available- " +movies.getNoOfTicketsAvailable());
//	    	if(movies.getNoOfTicketsAvailable() == 0) {
//	    		movies.setTicketsStatus("SOLD OUT");
//	    	}
//	    	else if (ticketsBooked >= movies.getNoOfTicketsAvailable()) {
//	            movies.setTicketsStatus("BOOK ASAP");
//	        } else {
//	            movies.setTicketsStatus("Seats Available");
//	        }
//	        movieService.saveMovie(movies);
//	    }
////	    kafkaTemplate.send(topic.name(),"tickets status upadated by the Admin for movie "+movieName);
//	    return new ResponseEntity<>("Ticket status updated successfully", HttpStatus.OK);
//	
//	}
    
    
//    @PutMapping("/{movieName}/update")
//	@PreAuthorize("hasRole('ADMIN')")
	public void updateTicketStatus(String movieName) {
	    List<Movie> movie = movieRepository.findByMovieName(movieName);
	    if (movie == null) {
	        throw new MoviesNotFound("Movie not found: " + movieName);
	    }
	
	    int ticketsBooked = movieService.getTotalNoTickets(movieName);
	    log.info("Total Tickets booked- " +ticketsBooked);
	    for (Movie movies : movie) {
	    	log.info("No. of Tickets available- " +movies.getNoOfTicketsAvailable());
	    	if(movies.getNoOfTicketsAvailable() == 0) {
	    		movies.setTicketsStatus("SOLD OUT");
	    	}
	    	else if (ticketsBooked >= movies.getNoOfTicketsAvailable()) {
	            movies.setTicketsStatus("BOOK ASAP");
	        } else {
	            movies.setTicketsStatus("AVAILABLE");
	        }
	        movieService.saveMovie(movies);
	    }
//	    kafkaTemplate.send(topic.name(),"tickets status upadated by the Admin for movie "+movieName);
//	    return new ResponseEntity<>("Ticket status updated successfully", HttpStatus.OK);
	
	}
    
    
//    @PutMapping("/{movieName}/update")
//	@Operation(summary="Update ticket status")
//	public ResponseEntity<String> upadteTicketStatus(@PathVariable String movieName,@RequestBody ObjectId ticket){
//		return new ResponseEntity<String>(movieService.updateTicketStatus(movieName,ticket),HttpStatus.OK);
//	}
	
	
	
	

    @DeleteMapping("/{movieName}/delete")@SecurityRequirement(name = "Bearer Authentication")
    @Operation(summary = "delete a movie(Admin Only)")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<String> deleteMovie(@PathVariable String movieName){
        List<Movie> availableMovies = movieService.findByMovieName(movieName);
        if(availableMovies.isEmpty()){
            throw new MoviesNotFound("No movies Available with moviename "+ movieName);
        }
        else {
            movieService.deleteByMovieName(movieName);
//            kafkaTemplate.send(topic.name(),"Movie Deleted by the Admin. "+movieName+" is now not available");
            return new ResponseEntity<>("Movie deleted successfully",HttpStatus.OK);
        }

    }
    
    
    //NEW-------------------------------------------------------------------------------
    
    
    
    @PostMapping("/add")
	public ResponseEntity addMovie(@RequestBody Movie movie){
		
		List<Movie> movieList=movieService.getAllMovies();
		
		if(movieList.isEmpty()) {
			
		}
		movieService.saveMovie(movie);
		return new ResponseEntity<>(HttpStatus.ACCEPTED);
	}
    
	@Autowired
	RoleRepository roleRepo;
	
	@GetMapping("/addrole")
	public ResponseEntity<?> addRoles(){
		Role admin = new Role(ERole.ROLE_ADMIN);
		Role user = new Role(ERole.ROLE_USER);

		roleRepo.saveAll(List.of(admin,user));
		
		return new ResponseEntity(roleRepo.findAll(),HttpStatus.OK);
	}
    
    
	//-------------------------------------------------------------------------------------
	
	
    @GetMapping("/helloworld")
    public String helloWorld(){
        log.debug("HELLO WORLD");
        return "Hello WOrld";
    }
    

    
    @GetMapping("/seats/{movieName}/{theaterName}")
    public List<String> seats(@PathVariable String movieName, @PathVariable String theaterName) {
        log.debug("SEATS Array");
        
        Movie movie = movieRepository.findByMovieNameAndTheaterName(movieName, theaterName);
        
        int totalSeats = movie.getTotalSeats();
        
        List<String> seats = new ArrayList<String>();
        
        List<String> a = Arrays.asList("A1", "A2", "A3", "A4", "A5", "A6", "A7", "A8", "A9", "A10");
        List<String> b = Arrays.asList("B1", "B2", "B3", "B4", "B5", "B6", "B7", "B8", "B9", "B10");
        List<String> c = Arrays.asList("C1", "C2", "C3", "C4", "C5", "C6", "C7", "C8", "C9", "C10");
        List<String> d = Arrays.asList("D1", "D2", "D3", "D4", "D5", "D6", "D7", "D8", "D9", "D10");
        List<String> e = Arrays.asList("E1", "E2", "E3", "E4", "E5", "E6", "E7", "E8", "E9", "E10");
        List<String> f = Arrays.asList("F1", "F2", "F3", "F4", "F5", "F6", "F7", "F8", "F9", "F10");
        List<String> g = Arrays.asList("G1", "G2", "G3", "G4", "G5", "G6", "G7", "G8", "G9", "G10");
        List<String> h = Arrays.asList("H1", "H2", "H3", "H4", "H5", "H6", "H7", "H8", "H9", "H10");
        List<String> i = Arrays.asList("I1", "I2", "I3", "I4", "I5", "I6", "I7", "I8", "I9", "I10");
        List<String> j = Arrays.asList("J1", "J2", "J3", "J4", "J5", "J6", "J7", "J8", "J9", "J10");
        List<String> k = Arrays.asList("K1", "K2", "K3", "K4", "K5", "K6", "K7", "K8", "K9", "K10");
        List<String> l = Arrays.asList("L1", "L2", "L3", "L4", "L5", "L6", "L7", "L8", "L9", "L10");
        List<String> m = Arrays.asList("M1", "M2", "M3", "M4", "M5", "M6", "M7", "M8", "M9", "M10");
        List<String> n = Arrays.asList("N1", "N2", "N3", "N4", "N5", "N6", "N7", "N8", "N9", "N10");
        List<String> o = Arrays.asList("O1", "O2", "O3", "O4", "O5", "O6", "O7", "O8", "O9", "O10");
        List<String> p = Arrays.asList("P1", "P2", "P3", "P4", "P5", "P6", "P7", "P8", "P9", "P10");
        List<String> q = Arrays.asList("Q1", "Q2", "Q3", "Q4", "Q5", "Q6", "Q7", "Q8", "Q9", "Q10");
        List<String> r = Arrays.asList("R1", "R2", "R3", "R4", "R5", "R6", "R7", "R8", "R9", "R10");
        List<String> s = Arrays.asList("S1", "S2", "S3", "S4", "S5", "S6", "S7", "S8", "S9", "S10");
        List<String> t = Arrays.asList("T1", "T2", "T3", "T4", "T5", "T6", "T7", "T8", "T9", "T10");
        List<String> u = Arrays.asList("U1", "U2", "U3", "U4", "U5", "U6", "U7", "U8", "U9", "U10");
        List<String> v = Arrays.asList("V1", "V2", "V3", "V4", "V5", "V6", "V7", "V8", "V9", "V10");
        List<String> w = Arrays.asList("W1", "W2", "W3", "W4", "W5", "W6", "W7", "W8", "W9", "W10");
        List<String> x = Arrays.asList("X1", "X2", "X3", "X4", "X5", "X6", "X7", "X8", "X9", "X10");
        List<String> y = Arrays.asList("Y1", "Y2", "Y3", "Y4", "Y5", "Y6", "Y7", "Y8", "Y9", "Y10");
        List<List<String>> llist = Arrays.asList(a,b,c,d,e,f,g,h,i,j,k,l,m,n,o,p,q,r,s,t,u,v,w,x,y);
        
        
//        List<Movie> availableMovies = movieService.findByMovieName(movieName);
//        
//        if(availableMovies.isEmpty()){
//            throw new MoviesNotFound("No movies Available with moviename "+ movieName);
//        }
//        else {
        
        int totalRows = totalSeats / 10;
        
        for(int row=0; row<10; ++row) {
        	for(int col=0; col<totalRows; ++col) {
        		seats.add(llist.get(col).get(row));
        	}
        }
        return seats;
    }
    
    
    @GetMapping("/bookedSeats/{movieName}/{theaterName}")
    public List<String> bookedSeats(@PathVariable String movieName, @PathVariable String theaterName ){
        log.debug("Getting Booked Seats");
        List<String> bookedSeats = new ArrayList<String>();
        
        List<Ticket> allTickets = movieService.findSeats(movieName, theaterName);
        for(Ticket each : allTickets){ 
                bookedSeats.addAll(each.getSeatNumber());
        }
        return bookedSeats;
    }
    
    

}
