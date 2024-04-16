package com.pyramix.web.gl;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.ZoneId;
import java.time.temporal.TemporalAdjusters;
import java.util.ArrayList;
import java.util.LinkedHashSet;
import java.util.List;

import org.apache.log4j.Logger;
import org.zkoss.zk.ui.event.Event;
import org.zkoss.zul.Datebox;
import org.zkoss.zul.ListModelList;
import org.zkoss.zul.Listbox;
import org.zkoss.zul.Listcell;
import org.zkoss.zul.Listfooter;
import org.zkoss.zul.Listitem;
import org.zkoss.zul.ListitemRenderer;

import com.pyramix.domain.coa.Coa_05_Master;
import com.pyramix.domain.gl.Balance;
import com.pyramix.domain.gl.GeneralLedger;
import com.pyramix.persistence.coa.dao.Coa_05_AccountMasterDao;
import com.pyramix.persistence.gl.dao.BalanceDao;
import com.pyramix.persistence.gl.dao.GeneralLedgerDao;
import com.pyramix.web.common.GFCBaseController;

import jakarta.persistence.NoResultException;

public class JournalActivityController extends GFCBaseController {

	/**
	 * 
	 */
	private static final long serialVersionUID = 6967955773572219477L;

	private GeneralLedgerDao generalLedgerDao;
	private BalanceDao balanceDao;
	private Coa_05_AccountMasterDao coa_05_AccountMasterDao;
	
	private Datebox startDatebox, endDatebox;
	private Listbox coaListbox, glListbox;
	
	private List<GeneralLedger> glList;
	private LinkedHashSet<Coa_05_Master> coaMasterSet;
	private ListModelList<Coa_05_Master> coaMasterListModelList;
	private ListModelList<GeneralLedger> glListModelList;
	private ZoneId zoneId = getZoneId();
	private LocalDate todayDate = getLocalDate(zoneId);
	private LocalDate startDate, endDate;
	private Balance accountCloseLastMonth;
	private BigDecimal totalDebit;
	private BigDecimal totalCredit;
	
	private static final Logger log = Logger.getLogger(JournalActivityController.class);
	
	public void onCreate$journalActivityPanel(Event event) throws Exception {
		log.info("journalActivityPanel created");
		
		setupThisMonthFirstDateEndDate();
		
		listCoaActivities();
	}

	private void setupThisMonthFirstDateEndDate() {
		startDate = todayDate.with(TemporalAdjusters.firstDayOfMonth());
		startDatebox.setValue(asDate(startDate, zoneId));
		startDatebox.setLocale(getLocale());
		
		endDate = todayDate.with(TemporalAdjusters.lastDayOfMonth());
		endDatebox.setValue(asDate(endDate, zoneId));
		endDatebox .setLocale(getLocale());
	}

	private void listCoaActivities() throws Exception {
		coaMasterSet = new LinkedHashSet<Coa_05_Master>();

		// permanent account MUST ALWAYS listed 
		List<Coa_05_Master> masterCoaList = getCoa_05_AccountMasterDao().findAllCoa_05_Master();
		masterCoaList.sort((c1, c2) -> {
			return c1.getMasterCoaComp().compareTo(
					c2.getMasterCoaComp());
		});
		List<Coa_05_Master> permanentCoaList = new ArrayList<Coa_05_Master>();
		for (Coa_05_Master coaMaster:masterCoaList) {
			if (coaMaster.getTypeCoaNumber()<4) {
				permanentCoaList.add(coaMaster);
			}
		}
		coaMasterSet.addAll(permanentCoaList);
		
		// active non-permanent account this month
		LinkedHashSet<Coa_05_Master> activeCoaSet = new LinkedHashSet<Coa_05_Master>();		
		glList = getGeneralLedgerDao().findAllGeneralLedgerByStartEndDate(asDate(startDate, zoneId), asDate(endDate, zoneId));
		glList.sort((c1, c2) -> {
			return c1.getMasterCoa().getMasterCoaComp().compareTo(
					c2.getMasterCoa().getMasterCoaComp());
		});
		for (GeneralLedger gl : glList) {
			if (gl.getMasterCoa().getTypeCoaNumber()>4) {
				activeCoaSet.add(gl.getMasterCoa());
			}
		}
		coaMasterSet.addAll(activeCoaSet);
		
		coaMasterListModelList = new ListModelList<Coa_05_Master>(coaMasterSet);
		
		coaListbox.setModel(coaMasterListModelList);
		coaListbox.setItemRenderer(coaMasterListitemRenderer());
	}

	private ListitemRenderer<Coa_05_Master> coaMasterListitemRenderer() {
		
		return new ListitemRenderer<Coa_05_Master>() {
			
			@Override
			public void render(Listitem item, Coa_05_Master coa, int index) throws Exception {
				Listcell lc;
				
				// Chart of Account
				lc = new Listcell(coa.getMasterCoaComp()+"-"+coa.getMasterCoaName());
				lc.setStyle("white-space:nowrap;");
				lc.setParent(item);
				
				item.setValue(coa);
			}
		};
	}
	
	public void onClick$reportButton(Event event) throws Exception {
		Listitem selItem = coaListbox.getSelectedItem();
		
		if (selItem==null) {
			throw new Exception("Chart of Account NOT Selected.");
		}
		
		Coa_05_Master selCoaMaster = selItem.getValue();
		log.info("Selected CoA: "+selCoaMaster.toString());
		
		// reset
		accountCloseLastMonth = null;
		// reset
		totalDebit = BigDecimal.ZERO;
		totalCredit = BigDecimal.ZERO;
		
		// check for closing
		checkAccountClosing(selCoaMaster);
		
		listJournalActivities(selCoaMaster);		
	}

	private void checkAccountClosing(Coa_05_Master selCoaMaster) throws Exception {
		// only permanent accounts need balance from last month
		if (selCoaMaster.getTypeCoaNumber()<4) {
			// last month date
			LocalDate lastMonthDate = todayDate.minusDays(30);
			// last month end date
			LocalDate lastMonthDateEnd = lastMonthDate.with(TemporalAdjusters.lastDayOfMonth());
			try {
				accountCloseLastMonth =
						getBalanceDao().findAccountBalanceByCoa_ClosingDate(selCoaMaster, 
								asDate(lastMonthDateEnd, zoneId));							
			} catch (NoResultException e) {
				// throw new Exception(e.getMessage());
			}
			
//			if (accountCloseLastMonth==null) {
//				throw new Exception("No Closing Balance at the end of last month.");
//			} else {
//				log.info(accountCloseLastMonth.toString());				
//			}
		} else {
			log.info("non permanent account - no need balance from last month");
		}
		
	}

	private void listJournalActivities(Coa_05_Master selCoaMaster) throws Exception {
		List<GeneralLedger> glList = 
				getGeneralLedgerDao().findAllGeneralLedgerByCoaMaster(selCoaMaster, 
						asDate(startDate, zoneId), asDate(endDate, zoneId));

		BigDecimal totalGLDebit = BigDecimal.ZERO;
		BigDecimal totalGLCredit = BigDecimal.ZERO;
		
		if (accountCloseLastMonth!=null) {
			// add a new row for beginning balance
			closingLastMonth(selCoaMaster, glList);			
		}
		
		// calc the total GL debit and credit
		for (GeneralLedger gl:glList) {
			totalGLDebit = totalGLDebit.add(gl.getDebitAmount());
			totalGLCredit = totalGLCredit.add(gl.getCreditAmount());
		}

		// add a new row for ending balance
		closingThisMonth(selCoaMaster, glList, totalGLDebit, totalGLCredit);			
		
		// calculate all for the footer
		for (GeneralLedger gl:glList) {
			totalDebit = totalDebit.add(gl.getDebitAmount());
			totalCredit = totalCredit.add(gl.getCreditAmount());
		}

		glListModelList = new ListModelList<GeneralLedger>(glList);
		
		glListbox.setModel(glListModelList);
		glListbox.setItemRenderer(glListitemRenderer());
		
	}

	private void closingThisMonth(Coa_05_Master selCoaMaster, List<GeneralLedger> glList, BigDecimal totalDebit,
			BigDecimal totalCredit) {
		// create ending balance this month
		GeneralLedger glEnd = new GeneralLedger();
		glEnd.setTransactionDate(asDate(todayDate, zoneId));
		glEnd.setTransactionDescription("Ending Balance");
		if (selCoaMaster.isCreditAccount()) {
			glEnd.setDebitAmount(totalCredit.subtract(totalDebit));			
			glEnd.setCreditAmount(BigDecimal.ZERO);
		} else {
			glEnd.setDebitAmount(BigDecimal.ZERO);			
			glEnd.setCreditAmount(totalDebit.subtract(totalCredit));
		}
		// insert balance from last month
		glList.add(glList.size(), glEnd);
	}
 
	private void closingLastMonth(Coa_05_Master selCoaMaster, List<GeneralLedger> glList) {
		// create account closing from last moth
		GeneralLedger glStart = new GeneralLedger();
		glStart.setTransactionDate(asDate(todayDate.with(TemporalAdjusters.firstDayOfMonth()), zoneId));
		glStart.setTransactionDescription("Beginning Balance");
		if (selCoaMaster.isCreditAccount()) {
			glStart.setCreditAmount(accountCloseLastMonth.getBalanceAmount());			
			glStart.setDebitAmount(BigDecimal.ZERO);
		} else {
			glStart.setCreditAmount(BigDecimal.ZERO);			
			glStart.setDebitAmount(accountCloseLastMonth.getBalanceAmount());
		}
		
		// insert balance from last month
		glList.add(0, glStart);
	}

	private ListitemRenderer<GeneralLedger> glListitemRenderer() {
		
		return new ListitemRenderer<GeneralLedger>() {
			
			@Override
			public void render(Listitem item, GeneralLedger gl, int index) throws Exception {
				Listcell lc;
				
				// Transaction Date
				lc = new Listcell(dateToStringDisplay(asLocalDate(gl.getTransactionDate()),
						"yyyy-MM-dd", getLocale()));
				lc.setParent(item);
				
				// Transaction Description
				lc = new Listcell(gl.getTransactionDescription());
				lc.setParent(item);
				
				// Debit(Rp.)
				lc = new Listcell(toDecimalFormat(gl.getDebitAmount(), getLocale(), "###.###.###,-"));
				lc.setValue(gl.getDebitAmount());
				lc.setParent(item);
				
				// Credit(Rp.)
				lc = new Listcell(toDecimalFormat(gl.getCreditAmount(), getLocale(), "###.###.###,-"));
				lc.setValue(gl.getCreditAmount());
				lc.setParent(item);
			}
		};
	}
	
	public void onAfterRender$glListbox(Event event) throws Exception {		
		Listfooter lf2 =
				(Listfooter) glListbox.getListfoot().getChildren().get(2);
		lf2.setLabel(toDecimalFormat(totalDebit, getLocale(), "###.###.###,-"));
		Listfooter lf3 =
				(Listfooter) glListbox.getListfoot().getChildren().get(3);
		lf3.setLabel(toDecimalFormat(totalCredit, getLocale(), "###.###.###,-"));

	}

	public GeneralLedgerDao getGeneralLedgerDao() {
		return generalLedgerDao;
	}

	public void setGeneralLedgerDao(GeneralLedgerDao generalLedgerDao) {
		this.generalLedgerDao = generalLedgerDao;
	}

	public Coa_05_AccountMasterDao getCoa_05_AccountMasterDao() {
		return coa_05_AccountMasterDao;
	}

	public void setCoa_05_AccountMasterDao(Coa_05_AccountMasterDao coa_05_AccountMasterDao) {
		this.coa_05_AccountMasterDao = coa_05_AccountMasterDao;
	}

	public BalanceDao getBalanceDao() {
		return balanceDao;
	}

	public void setBalanceDao(BalanceDao balanceDao) {
		this.balanceDao = balanceDao;
	}
	
	
	
}
