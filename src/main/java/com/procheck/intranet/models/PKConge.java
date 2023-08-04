
package com.procheck.intranet.models;


import java.time.LocalDate;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;

import javax.persistence.Table;

import com.fasterxml.jackson.annotation.JsonIgnore;

import lombok.*;


@Entity
@Table(name = "t_conge", schema = "public")
@EqualsAndHashCode(callSuper = true, onlyExplicitlyIncluded = true)
@AllArgsConstructor @NoArgsConstructor
@ToString
@Setter
@Getter
public class PKConge extends BaseEntity  {

    @Column(name = "s_status")
    private String status;
    @Column(name = "d_date_conge")
    private LocalDate dateDebut;
    @Column(name = "d_date_reprise")
    private LocalDate dateReprise;
    @Column(name = "d_date_signature")
    private LocalDate dateSignature;
    @Column(name = "f_nombre_jour")
    private int nombreJour;
    @Column(name = "n_conge_party")
    private Integer nCongeParty;
    @Column(name ="s_description")
    private String description;
    @Column(name = "s_name")
    private String name;

    @ManyToOne(cascade = CascadeType.MERGE)
    @JoinColumn(name="nIdTypeConge")
    private PKTypeConge typeConge;

    @ManyToOne(cascade = CascadeType.ALL)
    @JoinColumn(name="nIdDemande")
    @JsonIgnore
    private PKDemande demande;
}
