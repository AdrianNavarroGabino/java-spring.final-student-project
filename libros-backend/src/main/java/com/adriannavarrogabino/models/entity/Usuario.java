package com.adriannavarrogabino.models.entity;

import java.io.Serializable;
import java.util.Date;
import java.util.List;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.JoinTable;
import javax.persistence.ManyToMany;
import javax.persistence.OneToMany;
import javax.persistence.PrePersist;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;
import javax.persistence.UniqueConstraint;
import javax.validation.constraints.Email;
import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.Size;

import org.springframework.format.annotation.DateTimeFormat;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

@Entity
@Table(name = "usuarios")
public class Usuario implements Serializable {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;

	@NotEmpty(message = "El usuario no puede estar vacío")
	@Size(min = 3, max = 20, message =
			"El tamaño del nombre tiene que estar entre 3 y 20")
	@Column(nullable = false, unique = true)
	private String username;

	@NotEmpty(message = "La contraseña no puede estar vacía")
	@Column(length = 60)
	private String password;

	private boolean enabled;

	@NotEmpty(message = "El nombre no puede estar vacío")
	@Size(min = 3, max = 25, message =
			"El tamaño del nombre tiene que estar entre 3 y 25")
	@Column(nullable = false)
	private String nombre;

	@NotEmpty(message = "Los apellidos no pueden estar vacíos")
	@Size(min = 3, max = 25, message =
			"El tamaño de los apellidos tiene que estar entre 3 y 25")
	@Column(nullable = false)
	private String apellidos;

	@NotEmpty(message = "El email no puede estar vacío")
	@Email(message = "El email no es una dirección de correo bien formada")
	@Column(nullable = false, unique = true)
	private String correo;

	@Column(name = "fecha_nacimiento")
	@DateTimeFormat(pattern = "yyyy-MM-dd")
	@Temporal(TemporalType.DATE)
	private Date fechaNacimiento;

	@Column(name = "ultimo_acceso")
	@DateTimeFormat(pattern = "yyyy-MM-dd hh:mm:ss")
	@Temporal(TemporalType.TIMESTAMP)
	private Date ultimoAcceso;

	@Column(name = "acceso_actual")
	@DateTimeFormat(pattern = "yyyy-MM-dd hh:mm:ss")
	@Temporal(TemporalType.TIMESTAMP)
	private Date accesoActual;

	@JsonIgnoreProperties({ "hibernateLazyInitializer", "handler" })
	@OneToMany(fetch = FetchType.LAZY, cascade = CascadeType.ALL)
	@JoinColumn(name = "usuario_id")
	private List<Estanteria> estanterias;

	@ManyToMany(fetch = FetchType.LAZY, cascade = CascadeType.MERGE)
	@JoinTable(name = "usuarios_roles",
		joinColumns = @JoinColumn(name = "usuario_id"),
		inverseJoinColumns = @JoinColumn(name = "rol_id"), uniqueConstraints = {
			@UniqueConstraint(columnNames = { "usuario_id", "rol_id" }) })
	private List<Role> roles;

	@JsonIgnoreProperties(value = {
			"seguidos", "hibernateLazyInitializer", "handler"},
			allowSetters = true)
	@ManyToMany(fetch = FetchType.LAZY, cascade = CascadeType.MERGE)
	@JoinTable(name = "usuarios_seguir",
		joinColumns = @JoinColumn(name = "seguidor"),
		inverseJoinColumns = @JoinColumn(name = "seguido"),
		uniqueConstraints = {
			@UniqueConstraint(columnNames = { "seguidor", "seguido" }) })
	private List<Usuario> seguidos;

	@OneToMany(fetch = FetchType.LAZY, cascade = CascadeType.ALL)
	@JoinColumn(name = "usuario_id")
	private List<Notificacion> notificaciones;

	@PrePersist
	public void prePersist() {
		this.ultimoAcceso = new Date();
		this.accesoActual = new Date();
	}

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public String getNombre() {
		return nombre;
	}

	public void setNombre(String nombre) {
		this.nombre = nombre;
	}

	public String getApellidos() {
		return apellidos;
	}

	public void setApellidos(String apellidos) {
		this.apellidos = apellidos;
	}

	public String getUsername() {
		return username;
	}

	public void setUsername(String username) {
		this.username = username;
	}

	public String getCorreo() {
		return correo;
	}

	public void setCorreo(String correo) {
		this.correo = correo;
	}

	public String getPassword() {
		return password;
	}

	public void setPassword(String password) {
		this.password = password;
	}

	public Date getFechaNacimiento() {
		return fechaNacimiento;
	}

	public void setFechaNacimiento(Date fechaNacimiento) {
		this.fechaNacimiento = fechaNacimiento;
	}

	public Date getUltimoAcceso() {
		return ultimoAcceso;
	}

	public void setUltimoAcceso(Date ultimoAcceso) {
		this.ultimoAcceso = ultimoAcceso;
	}

	public Date getAccesoActual() {
		return accesoActual;
	}

	public void setAccesoActual(Date accesoActual) {
		this.accesoActual = accesoActual;
	}

	public boolean isEnabled() {
		return enabled;
	}

	public void setEnabled(boolean enabled) {
		this.enabled = enabled;
	}

	public List<Estanteria> getEstanterias() {
		return estanterias;
	}

	public void setEstanterias(List<Estanteria> estanterias) {
		this.estanterias = estanterias;
	}

	public void addEstanteria(Estanteria estanteria) {
		this.estanterias.add(estanteria);
	}

	public List<Role> getRoles() {
		return roles;
	}

	public void setRoles(List<Role> roles) {
		this.roles = roles;
	}

	public List<Usuario> getSeguidos() {
		return seguidos;
	}

	public void setSeguidos(List<Usuario> seguidos) {
		this.seguidos = seguidos;
	}

	public List<Notificacion> getNotificaciones() {
		return notificaciones;
	}

	public void setNotificaciones(List<Notificacion> notificaciones) {
		this.notificaciones = notificaciones;
	}

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
}
