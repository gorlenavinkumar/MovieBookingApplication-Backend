package com.rbp.bookmymovie.models;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.bson.types.ObjectId;
import org.springframework.data.mongodb.core.mapping.Document;

@Document(value = "movie")
@Data
@AllArgsConstructor
@NoArgsConstructor
public class Movie {
    private ObjectId _id;
    private String movieName;

    private String theatreName;
    private Integer noOfTicketsAvailable;
    private Integer totalSeats;
    private String imageUrl;
    private String ticketsStatus;

    public Movie(String movieName, String theatreName, Integer totalSeats, Integer noOfTicketsAvailable, String ticketsStatus,String imageUrl) {
        this.movieName = movieName;
        this.theatreName = theatreName;
        this.totalSeats = totalSeats;
        this.noOfTicketsAvailable = noOfTicketsAvailable;
        this.ticketsStatus = ticketsStatus;
        this.imageUrl =imageUrl;
    }

  

    public Movie(String movieName, String theatreName, Integer noOfTicketsAvailable,String imageUrl) {
        this.movieName = movieName;
        this.theatreName = theatreName;
        this.noOfTicketsAvailable = noOfTicketsAvailable;
        this.imageUrl = imageUrl;
    }

    public Movie(ObjectId _id, String movieName, String theatreName, Integer noOfTicketsAvailable,String imageUrl) {
        this._id = _id;
        this.movieName = movieName;
        this.theatreName = theatreName;
        this.noOfTicketsAvailable = noOfTicketsAvailable;
        this.imageUrl =imageUrl;
    }



	public Movie(String string, String string2, int i, int j, String string3) {
		// TODO Auto-generated constructor stub
	}
}
