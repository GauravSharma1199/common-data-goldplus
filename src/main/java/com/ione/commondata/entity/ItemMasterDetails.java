package com.ione.commondata.entity;


//import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.DynamicInsert;
import org.hibernate.annotations.DynamicUpdate;

import javax.persistence.*;


@Entity
@DynamicInsert
@DynamicUpdate
@Data
@NoArgsConstructor
@AllArgsConstructor

@Table(name="ItemMasterDetails")
public class ItemMasterDetails {
	
	@Id
	@GeneratedValue(strategy = GenerationType.AUTO)
	private int itemId;
	
	@Column(name = "itemName")
	private String itemName;
	
	
	@Column(name = "ItemDESC")
	private String ItemDESC;
	

}
