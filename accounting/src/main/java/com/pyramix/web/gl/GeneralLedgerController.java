package com.pyramix.web.gl;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.Month;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.time.temporal.TemporalAdjusters;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Properties;
import java.util.Set;

import org.apache.log4j.Logger;
import org.jxls.builder.JxlsOutputFile;
import org.jxls.transform.poi.JxlsPoiTemplateFillerBuilder;
import org.zkoss.zk.ui.event.Event;
import org.zkoss.zul.Combobox;
import org.zkoss.zul.Comboitem;
import org.zkoss.zul.Datebox;
import org.zkoss.zul.Filedownload;
import org.zkoss.zul.Label;
import org.zkoss.zul.ListModelList;
import org.zkoss.zul.Listbox;
import org.zkoss.zul.Listcell;
import org.zkoss.zul.Listitem;
import org.zkoss.zul.ListitemRenderer;
import org.zkoss.zul.Tab;
import org.zkoss.zul.Tabbox;
import org.zkoss.zul.Tabs;
import org.zkoss.zul.Timer;

import com.pyramix.domain.coa.Coa_01_AccountType;
import com.pyramix.domain.coa.Coa_05_Master;
import com.pyramix.domain.gl.GeneralLedger;
import com.pyramix.persistence.coa.dao.Coa_01_AccountTypeDao;
import com.pyramix.persistence.gl.dao.GeneralLedgerDao;
import com.pyramix.web.common.GFCBaseController;

public class GeneralLedgerController extends GFCBaseController {

	/**
	 * 
	 */
	private static final long serialVersionUID = -4132805928767688796L;

	private GeneralLedgerDao generalLedgerDao;
	private Coa_01_AccountTypeDao coa_01_AccountTypeDao;
	
	private Combobox periodCombobox, coaMasterCombobox;
	private Datebox startDatebox, endDatebox;
	private Listbox generalLedgerListbox;
	private Label totalLabel, exportExcelLabel;
	private Tabbox coaTabbox;
	private Timer timer;
	
	private List<GeneralLedger> generalLedgers;
	private List<Coa_05_Master> coaMasterList;
	private ZoneId zoneId = getZoneId();
	private String gen_xlsx_path;
	
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
		
		// set coa type for the tabbox
		setupCoaTypeTabbox();

		// set to tab index 0 ('All')
		coaTabbox.setSelectedIndex(0);
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
	
	private void setupCoaTypeTabbox() throws Exception {
		Tabs tabs = new Tabs();
		tabs.setParent(coaTabbox);
		
		Tab tab;
		
		// 'All' tab
		tab = new Tab();
		tab.setLabel("All");
		tab.setValue(null);
		tab.setParent(tabs);		
		
		List<Coa_01_AccountType> accountTypeList =
				getCoa_01_AccountTypeDao().findAllCoa_01_AccountType();
		
		for (Coa_01_AccountType accountType : accountTypeList) {
			tab = new Tab();		
			tab.setLabel(accountType.getAccountTypeName());
			tab.setValue(accountType);
			tab.setParent(tabs);			
		}

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
	
	private void listGeneralLedgersByAccountType(Coa_01_AccountType accountType) throws Exception {
		generalLedgers =
				getGeneralLedgerDao().findAllGeneralLedgerByStartEndDate(
						startDatebox.getValue(), endDatebox.getValue());
		
		List<GeneralLedger> glByAccountTypeList = new ArrayList<GeneralLedger>();
		
		for (GeneralLedger gl : generalLedgers) {
			if (gl.getMasterCoa().getTypeCoaNumber()==accountType.getAccountTypeNumber()) {
				glByAccountTypeList.add(gl);
			}
		}
		
		generalLedgers.clear();
		generalLedgers.addAll(glByAccountTypeList);
		
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
		Collections.sort(
				coaMasterList, 
				Comparator.comparing(Coa_05_Master::getMasterCoaComp));
		
		// coaMasterList.forEach(c -> log.info(c.getMasterCoaComp()));
	}	
	
	private void setupCoaMasterCombobox() {
		// reset
		coaMasterCombobox.getItems().clear();
		
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
		// coaTabbox reset
		coaTabbox.setSelectedIndex(0);
		
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
		// set to tab "All"
		// coaTabbox.setSelectedIndex(0);
		
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
	
	public void onSelect$coaTabbox(Event event) throws Exception {
		int tabIndex = coaTabbox.getSelectedIndex();

		// reset the filter account
		coaMasterCombobox.setSelectedIndex(0);
		
		if (tabIndex==0) {
			log.info("All tab selected...");
			
			// all
			listGeneralLedgers();
			
			// find all coa
			findAllMasterCoa();
			
			// populate the combobox
			setupCoaMasterCombobox();
			coaMasterCombobox.setSelectedIndex(0);			
			
		} else {
			Tab selTab = coaTabbox.getSelectedTab();
			
			// value of the selected tab
			Coa_01_AccountType accountType = selTab.getValue();
			log.info(accountType.getAccountTypeName()+" tab selected...");
			
			// list by accountType
			listGeneralLedgersByAccountType(accountType);
			
			// find all coa
			findAllMasterCoa();
			
			// populate the combobox
			setupCoaMasterCombobox();
			coaMasterCombobox.setSelectedIndex(0);
			
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
	
	public void onClick$exportExcelButton(Event event) throws Exception {
		exportExcelLabel.setValue("Please wait...");
		// set filepath for generated file and export destination
		setFilePath();
		
		// NOTE: Jxls NOT working with Java Record
		
		// setup filename with timestamp
		LocalDateTime currentDateTime = getLocalDateTime(zoneId);
		DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyyMMddHHmmss", getLocale());
		String timestamp = currentDateTime.format(formatter);
		// log.info(timestamp);
		String filename = "generalLedger"+timestamp+".xlsx";
		
		List<LedgerReport> ledgerReports = new ArrayList<LedgerReport>();
		LedgerReport ledgerReport;
		for (GeneralLedger gl : generalLedgers) {
			ledgerReport = new LedgerReport(
						"'"+dateToStringDisplay(asLocalDate(gl.getTransactionDate()), "yyyy-MM-dd", getLocale()),
						gl.getMasterCoa().getMasterCoaComp()+"-"+gl.getMasterCoa().getMasterCoaName(),
						gl.getTransactionDescription(),
						gl.getDebitAmount(),
						gl.getCreditAmount()
					);
			ledgerReports.add(ledgerReport);
		}
		
		ledgerReports.forEach(l -> log.info(l.toString()));
		
		// file output info
		log.info("Export to "+gen_xlsx_path+filename);
		
		File xlsxFile = new File(gen_xlsx_path+filename);
		
		Map<String, Object> data = new HashMap<>();
		data.put("ledgerReports", ledgerReports);
		JxlsPoiTemplateFillerBuilder.newInstance()
			.withTemplate(gen_xlsx_path+"template.xlsx")
			.build()
			.fill(data, new JxlsOutputFile(xlsxFile));

		Filedownload.save(xlsxFile, "application/vnd.openxmlformats-officedocument.spreadsheetml.sheet");
		
		exportExcelLabel.setValue("Export to "+gen_xlsx_path+filename);
		timer.start();		
	}
	
	public void onTimer$timer(Event event) throws Exception {
		exportExcelLabel.setValue("");
	}
	
	public void setFilePath() throws Exception {
		try (InputStream input = new FileInputStream("/pyramix/filepath.properties")) {
			Properties prop = new Properties();
			
			prop.load(input);
			
			gen_xlsx_path = prop.getProperty("generated_xlsx_path");
			
			log.info(gen_xlsx_path);
			
		} catch (IOException io) {
			throw io;
		}
	}
	
	public GeneralLedgerDao getGeneralLedgerDao() {
		return generalLedgerDao;
	}

	public void setGeneralLedgerDao(GeneralLedgerDao generalLedgerDao) {
		this.generalLedgerDao = generalLedgerDao;
	}

	public Coa_01_AccountTypeDao getCoa_01_AccountTypeDao() {
		return coa_01_AccountTypeDao;
	}

	public void setCoa_01_AccountTypeDao(Coa_01_AccountTypeDao coa_01_AccountTypeDao) {
		this.coa_01_AccountTypeDao = coa_01_AccountTypeDao;
	}
}
