package it.uniroma3.siw.model;

import java.util.List;
import java.util.Objects;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.ManyToMany;
import javax.persistence.OneToMany;
import javax.persistence.OneToOne;

@Entity
public class Artist {
	@Id
	@GeneratedValue(strategy = GenerationType.AUTO)
	private Long id;
	private String nome;
	private String cognome;
	private String bornDate;
	private String deathDate;
	@OneToOne
	private Image image;
	@OneToMany(mappedBy="director")
	private List<Movie> directed;
	@ManyToMany
	private List<Movie> acted;
	
	public Long getId() {
		return id;
	}
	public void setId(Long id) {
		this.id = id;
	}
	public String getNome() {
		return nome;
	}
	public void setNome(String nome) {
		this.nome = nome;
	}
	public String getCognome() {
		return cognome;
	}
	public void setCognome(String cognome) {
		this.cognome = cognome;
	}
	public String getBornDate() {
		return bornDate;
	}
	public void setBornDate(String bornDate) {
		this.bornDate = bornDate;
	}
	public String getDeathDate() {
		return deathDate;
	}
	public void setDeathDate(String deathDate) {
		this.deathDate = deathDate;
	}
	public Image getImage() {
		return image;
	}
	public void setImage(Image image) {
		this.image = image;
	}
	@Override
	public int hashCode() {
		return Objects.hash(bornDate, cognome, nome);
	}
	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		Artist other = (Artist) obj;
		return Objects.equals(bornDate, other.bornDate) && Objects.equals(cognome, other.cognome)
				&& Objects.equals(nome, other.nome);
	}
}
