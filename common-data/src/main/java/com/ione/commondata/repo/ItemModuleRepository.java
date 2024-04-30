package com.ione.commondata.repo;

import com.ione.commondata.entity.ItemMasterDetails;
//import jakarta.transaction.Transactional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Component;

import javax.transaction.Transactional;
import java.sql.Clob;

@Component
public interface ItemModuleRepository extends JpaRepository<ItemMasterDetails, Integer> {

	@Query(value = "select xx_item_creation_payload() from  dual", nativeQuery = true)
	public Clob fetchItemRecords();

	@Query(value = "select xx_item_updation_payload from  dual", nativeQuery = true)
	public Clob updateItemRecords();

	@Query(value = "Select xx_sr_creation_payload() from dual", nativeQuery = true)
	public Clob salesRecordCreation();
	
	@Query(value = "Select xx_sr_updation_payload() from dual", nativeQuery = true)
	public Clob salesRecordUpdate();
	
	@Query(value = "Select xx_dispatch_payload() from dual", nativeQuery = true)
	public Clob dispatchItemCreation();
	
	@Query(value = "Select xx_invoice_payload() from dual", nativeQuery = true)
	public Clob invoiceItemCreation();

	@Query(value = "Select XX_CN_DN_PAYLOAD() from dual", nativeQuery = true)
	public Clob getCreditDebitData();
	
	@Query(value = "Select xx_order_status_payload() from dual", nativeQuery = true)
	public Clob orderCreationPayload();
	
	@Query(value = "Select xx_order_status_upd_payload() from dual", nativeQuery = true)
	public Clob orderUpdatePayload();
	
	@Query(value = "Select XX_PAYMENT_PAYLOAD() from dual", nativeQuery = true)
	public Clob paymentPayload(); 
	
	@Query(value = "Select xx_payment_upd_payload() from dual", nativeQuery = true)
	public Clob paymentUpdatePayload();

	@Query(value = "Select XX_INVOICE_PDF_PAYLOAD() from dual", nativeQuery = true)
	public Clob pdfUploadPayload();

	@Query(value = "Select xx_customer_payload() from dual", nativeQuery = true)
	public Clob customerPayload();
	
	@Query(value = "Select xx_customer_update_payload() from dual", nativeQuery = true)
	public Clob customerUpdatePload();
	
	@Query(value = "Select xx_stock_payload() from dual", nativeQuery = true)
	public Clob stockCreationPayload();
	
	@Transactional
	@Modifying
	@Query(value = "call xxgp_crm_organization_map_prc(:p_item_id,:p_organization_id,:p_crm_id,:p_message)", nativeQuery = true)
	public void updateCRMID(@Param("p_item_id") String p_item_id, @Param("p_organization_id") String p_organization_id,
			@Param("p_crm_id") String p_crm_id, @Param("p_message") String p_message);

	@Transactional
	@Modifying
	@Query(value = "call xxgp_crm_update_statuas_prc(:p_crm_id,:p_message)", nativeQuery = true)
	public void updatedCRMIDRes(@Param("p_crm_id") String p_crm_id, @Param("p_message") String p_message);

	@Transactional
	@Modifying
	@Query(value = "call xxgp_crm_update_status_prc_sr(:p_crm_id,:p_message,:p_salesrep_id)", nativeQuery = true)
	public void updatedSalesItemRecords(@Param("p_crm_id") String p_crm_id, @Param("p_message") String p_message,@Param("p_salesrep_id") String p_salesrep_id);
	
	@Transactional
	@Modifying
	@Query(value = "call xxgp_crm_update_inv_prc(:p_order_number,:p_organization_id,:p_crm_id,:p_message)", nativeQuery = true)
	public void updatedInvoiceItemRecords(@Param("p_order_number") String p_order_number, @Param("p_organization_id") String p_organization_id,@Param("p_crm_id") String p_crm_id,@Param("p_message") String p_message);

	@Transactional
	@Modifying
	@Query(value = "call XXGP_CRM_UPDATE_CNDN_PRC(:p_receivable_id,:p_organization_id,:p_crm_id,:p_message)", nativeQuery = true)
	public void updateCRM(@Param("p_receivable_id") String p_receivable_id, @Param("p_organization_id") String p_organization_id,@Param("p_crm_id") String p_crm_id,@Param("p_message") String p_message);

	@Transactional
	@Modifying
	@Query(value = "call XXGP_CRM_UPDATE_FILES(:p_record_no,:p_status,:p_message)", nativeQuery = true)
	public void updatePDF(@Param("p_record_no") String p_record_no, @Param("p_status") String p_status,@Param("p_message") String p_message);

	@Transactional
	@Modifying
	@Query(value = "call XXGP_CRM_PENDING_UPDATE_CUST_FILES(:p_record_no,:p_status,:p_message)", nativeQuery = true)
	public void customerUpdateAck(@Param("p_record_no") String p_record_no, @Param("p_status") String p_status,@Param("p_message") String p_message);

	@Transactional
	@Modifying
	@Query(value = "call XXGP_CRM_ORDER_PENDING_UPD_FILES(:p_record_no,:p_status,:p_message)", nativeQuery = true)
	public void orderUpdateAck(@Param("p_record_no") String p_record_no, @Param("p_status") String p_status,@Param("p_message") String p_message);


	@Transactional
	@Modifying
	@Query(value = "call xxgp_crm_update_order_prc(:p_order_number,:p_crm_id,:p_message,:p_organization_id)", nativeQuery = true)
	public void updatedOrderCreationRecords(@Param("p_order_number") String p_order_number, @Param("p_crm_id") String p_crm_id,@Param("p_message") String p_message ,@Param("p_organization_id") String p_organization_id);
	
	@Transactional
	@Modifying
	@Query(value = "call xxgp_crm_update_orderline_prc(:p_order_number,:p_line_num,:p_crm_id,:p_message,:p_organization_id)", nativeQuery = true)
	public void updatedOrderLineRecords(@Param("p_order_number") String p_order_number,@Param("p_line_num") String p_line_num, @Param("p_crm_id") String p_crm_id,@Param("p_message") 
	String p_message,@Param("p_organization_id") String p_organization_id);
	
	@Transactional
	@Modifying
	@Query(value = "call xxgp_crm_update_payment_prc(:name,:p_organization_id,:invoice_no,:p_crm_id,:p_message)", nativeQuery = true)
	public void updatePaymentRecords(@Param("name") String name, @Param("p_organization_id") String p_organization_id,@Param("invoice_no") String invoice_no,@Param("p_crm_id") String p_crm_id,@Param("p_message") String p_message);

	@Transactional
	@Modifying
	@Query(value = "call  XXGP_CRM_UPDATE_CUSTOMER(:p_crm_id,:p_message,:p_location,:organization_id)", nativeQuery = true)
	public void updatedCustomerRecords(@Param("p_crm_id") String p_crm_id, @Param("p_message") String p_message,@Param("p_location") String p_location,@Param("organization_id") String organization_id);
	
	@Transactional
	@Modifying
	@Query(value = "call XXGP_ZOHO_INTEGRATION.xxgp_status_log_start(:name)", nativeQuery = true)
	public void updateItemCreationStart(@Param("name") String name );
	
	@Transactional
	@Modifying
	@Query(value = "call XXGP_ZOHO_INTEGRATION.xxgp_status_log_end(:name)", nativeQuery = true)
	public void updateItemCreationEnd(@Param("name") String name );
	
	@Transactional
	@Modifying
	@Query(value = "call xxgp_crm_update_disp_prc(:p_trx_line_id,:p_crm_id,:p_message)", nativeQuery = true)
	public void updateDispatchItem(@Param("p_trx_line_id") String p_trx_line_id,@Param("p_crm_id") String p_crm_id,@Param("p_message") String p_message );

	@Transactional
	@Modifying
	@Query(
			value = "call xxgpcrmintg.pkg_log.pc_lg_apicall(:p_id,:p_par_id,:p_api_name,:p_program_name,:p_content,:p_step_desc)",
			nativeQuery = true
	)
	public void pushLog(
			@Param("p_id") String p_id,
			@Param("p_par_id") String p_par_id,
			@Param("p_api_name") String p_api_name,
			@Param("p_program_name") String p_program_name,
			@Param("p_content") String p_content,
			@Param("p_step_desc") String p_step_desc
	);

}
