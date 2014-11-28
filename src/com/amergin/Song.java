package com.amergin;

public class Song {
	private String id;
	private String name;
	private String album;
	private String artist;
	public Song(String id, String name, String album, String artist) {
		SetValue( id,  name,  album,  artist);
	}
	public void SetValue(String id, String name, String album, String artist) {
		this.id = id;
		this.name = name;
		this.album = album;
		this.artist = artist;
	}
	
	
	public String getId() {
		return id;
	}
	public String getName() {
		return name;
	}
	public String getAlbum() {
		return album;
	}
	public void setArtist(String artist) {
		this.artist = artist;
	}
	
	
	
	
}
