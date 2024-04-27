package com.pyramix.web.gl;

import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.Month;
import java.time.ZoneId;
import java.time.temporal.TemporalAdjusters;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashSet;
import java.util.List;
import java.util.Properties;
import java.util.Set;

import org.apache.log4j.Logger;
import org.zkoss.zk.ui.event.Event;
import org.zkoss.zul.Combobox;
import org.zkoss.zul.Comboitem;
import org.zkoss.zul.Datebox;
import org.zkoss.zul.Label;
import org.zkoss.zul.ListModelList;
import org.zkoss.zul.Listbox;
import org.zkoss.zul.Listcell;
import org.zkoss.zul.Listitem;
import org.zkoss.zul.ListitemRenderer;

import com.pyramix.domain.coa.Coa_05_Master;
import com.pyramix.domain.gl.GeneralLedger;
import com.pyramix.persistence.gl.dao.GeneralLedgerDao;
import com.pyramix.web.common.GFCBaseController;

public class GeneralLedgerController extends GFCBaseController {

	/**
	 * 
	 */
	private static final long serialVersionUID = -4132805928767688796L;

	private GeneralLedgerDao generalLedgerDao;
	
	private Combobox periodCombobox, coaMasterCombobox;
	private Datebox startDatebox, endDatebox;
	private Listbox generalLedgerListbox;
	private Label totalLabel;
	
	private List<GeneralLedger> generalLedgers;
	private List<Coa_05_Master> coaMasterList;
	private ZoneId zoneId = getZoneId();
	
	private static final int START_YEAR = 2024;
	
	final private static String PROPERTIES_FILE_PATH="/pyramix/config.properties";
	
	private static final Logger log = Logger.getLogger(GeneralLedgerController.class);
	
	public void onCreate$generalLedgerPanel(Event event) throws Exception {
		log.info("generalLedgerPanel created");
		
		// periods
		listYearMonthPeriods();		
		
		// read config
		int selIndex = getConfigSelectedIndex();
		periodCombobox.setSelectedIndex(selIndex);
		
		setGeneralLedgerStartAndEndDate();
		
		// list
		listGeneralLedgers();
		
		// find all coa
		findAllMasterCoa();
		
		// populate the combobox
		setupCoaMasterCombobox();
		coaMasterCombobox.setSelectedIndex(0);
		
		// calc the total
		calculateTotalDebitCredit();
	}

	public void onCheck$defaultCheckbox(Event event) throws Exception {
		int selIndex =	periodCombobox.getSelectedIndex();
		
		try (InputStream input = new FileInputStream(PROPERTIES_FILE_PATH)) {
            Properties prop = new Properties();

            // load the properties file
            prop.load(input);
			// set the properties value
			prop.setProperty("generalledger_period_index", String.valueOf(selIndex));
			// save properties to project root folder
			prop.store(new FileOutputStream(PROPERTIES_FILE_PATH), null);
            
		} catch (IOException io) {
			io.printStackTrace();
		}		
	}
	
	private void listYearMonthPeriods() {
		Comboitem comboitem;
		for (int i=1; i<13; i++) {
			comboitem = new Comboitem();
			comboitem.setLabel(START_YEAR
					+"-"+i);
			comboitem.setParent(periodCombobox);
		}	
	}

	private int getConfigSelectedIndex() {
		String idxStr = "0";
		
		try (InputStream input = new FileInputStream(PROPERTIES_FILE_PATH)) {

            Properties prop = new Properties();

            // load the properties file
            prop.load(input);

            // get the property value and print it out
            idxStr = prop.getProperty("generalledger_period_index");
            log.info(idxStr);

		} catch (IOException ex) {
            ex.printStackTrace();
        }

		return Integer.valueOf(idxStr);
	}	

	private void setGeneralLedgerStartAndEndDate() {
		int selPeriod = periodCombobox.getSelectedIndex();
		
		// selPeriod to month
		Month month = Month.of(selPeriod+1);
		
		// set start and end LocalDate
		LocalDate startLocalDate = LocalDate.of(START_YEAR, month, 1);
		LocalDate endLocalDate = startLocalDate.with(TemporalAdjusters.lastDayOfMonth());
		
		startDatebox.setValue(asDate(startLocalDate, zoneId));
		endDatebox.setValue(asDate(endLocalDate, zoneId));
	}	
	
	private void listGeneralLedgers() throws Exception {
		generalLedgers =
				getGeneralLedgerDao().findAllGeneralLedgerByStartEndDate(
						startDatebox.getValue(), endDatebox.getValue());
		
		ListModelList<GeneralLedger> generalLedgerModelList =
				new ListModelList<GeneralLedger>(generalLedgers);
		generalLedgerListbox.setModel(generalLedgerModelList);
		generalLedgerListbox.setItemRenderer(getGeneralLedgerListitemRenderer());
	}

	private void listGeneralLedgersByCoa() throws Exception {
		Comboitem item = coaMasterCombobox.getSelectedItem();
		Coa_05_Master selCoaMaster = item.getValue();
		generalLedgers =
				getGeneralLedgerDao().findAllGeneralLedgerByCoaMaster(selCoaMaster,
						startDatebox.getValue(), endDatebox.getValue());
		
		ListModelList<GeneralLedger> generalLedgerModelList =
				new ListModelList<GeneralLedger>(generalLedgers);
		generalLedgerListbox.setModel(generalLedgerModelList);
		generalLedgerListbox.setItemRenderer(getGeneralLedgerListitemRenderer());
	}	
	
	private void findAllMasterCoa() {
		Set<Coa_05_Master> coaMasterSet = new HashSet<Coa_05_Master>();
		for (GeneralLedger gl : generalLedgers) {
			coaMasterSet.add(gl.getMasterCoa());
		}
		// covert to arraylist
		coaMasterList = new ArrayList<Coa_05_Master>(coaMasterSet);
		// sorting the list
		Collections.sort(coaMasterList, Comparator.comparing(Coa_05_Master::getMasterCoaComp));
		
		// coaMasterList.forEach(c -> log.info(c.getMasterCoaComp()));
	}	
	
	private void setupCoaMasterCombobox() {
		Comboitem comboitem;
		
		// all - default
		comboitem = new Comboitem();
		comboitem.setLabel("All");
		comboitem.setValue(null);
		comboitem.setParent(coaMasterCombobox);
		
		for (Coa_05_Master coaMaster : coaMasterList) {
			comboitem = new Comboitem();
			comboitem.setLabel(coaMaster.getMasterCoaComp()
					+"-"+coaMaster.getMasterCoaName());
			comboitem.setValue(coaMaster);
			comboitem.setParent(coaMasterCombobox);
		}
	}
	
	private ListitemRenderer<GeneralLedger> getGeneralLedgerListitemRenderer() {
		
		return new ListitemRenderer<GeneralLedger>() {
			
			@Override
			public void render(Listitem item, GeneralLedger gl, int index) throws Exception {
				Listcell lc;
				
				// Date
				lc = new Listcell(dateToStringDisplay(asLocalDate(gl.getTransactionDate()),
						"yyyy-MM-dd", getLocale()));
				lc.setParent(item);
				
				// COA
				lc = new Listcell(gl.getMasterCoa().getMasterCoaComp()+"-"
						+gl.getMasterCoa().getMasterCoaName());
				lc.setParent(item);
				
				// Description
				lc = new Listcell(gl.getTransactionDescription());
				lc.setParent(item);
				
				// Debit(Rp.)
				lc = new Listcell(toDecimalFormat(gl.getDebitAmount(), getLocale(), "###.###.###,-"));
				lc.setParent(item);
				
				// Credit(Rp.)
				lc = new Listcell(toDecimalFormat(gl.getCreditAmount(), getLocale(), "###.###.###,-"));
				lc.setParent(item);
			}
		};
	}

	public void onSelect$periodCombobox(Event event) throws Exception {
		// set start and end date
		setGeneralLedgerStartAndEndDate();
	}
	
	public void onClick$findGeneralLedgerButton(Event event) throws Exception {
		// re-list
		listGeneralLedgers();
		// find all coa
		findAllMasterCoa();
		
		// reset coaMasterCombobbox
		coaMasterCombobox.getItems().clear();
		
		// populate the combobox
		setupCoaMasterCombobox();
		coaMasterCombobox.setSelectedIndex(0);
		
		// calc the total
		calculateTotalDebitCredit();		
		
	}
	
	public void onClick$filterButton(Event event) throws Exception {
		if (coaMasterCombobox.getSelectedIndex()==0) {
			// all
			listGeneralLedgers();
		} else {
			// by COA
			listGeneralLedgersByCoa();			
		}
		// calc
		calculateTotalDebitCredit();
	}

	private void calculateTotalDebitCredit() throws Exception {
		// calc total Debit & Credit
		BigDecimal totalDebit = BigDecimal.ZERO;
		BigDecimal totalCredit = BigDecimal.ZERO;
		for (GeneralLedger gl : generalLedgers) {
			totalDebit = totalDebit.add(gl.getDebitAmount());
			totalCredit = totalCredit.add(gl.getCreditAmount());
		}
		
		totalLabel.setValue("Total Debit: "+toDecimalFormat(totalDebit, getLocale(), "###.###.###,-")
				+" "+"Total Credit: "+toDecimalFormat(totalCredit, getLocale(), "###.###.###,-"));
	}	
	
	public GeneralLedgerDao getGeneralLedgerDao() {
		return generalLedgerDao;
	}

	public void setGeneralLedgerDao(GeneralLedgerDao generalLedgerDao) {
		this.generalLedgerDao = generalLedgerDao;
	}
}
