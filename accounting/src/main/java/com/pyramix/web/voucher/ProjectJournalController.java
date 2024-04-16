package com.pyramix.web.voucher;

import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.Month;
import java.time.ZoneId;
import java.time.temporal.TemporalAdjusters;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Properties;

import org.apache.log4j.Logger;
import org.zkoss.zk.ui.Executions;
import org.zkoss.zk.ui.event.Event;
import org.zkoss.zk.ui.event.EventListener;
import org.zkoss.zk.ui.event.Events;
import org.zkoss.zul.Button;
import org.zkoss.zul.Combobox;
import org.zkoss.zul.Comboitem;
import org.zkoss.zul.Datebox;
import org.zkoss.zul.Decimalbox;
import org.zkoss.zul.Label;
import org.zkoss.zul.ListModelList;
import org.zkoss.zul.Listbox;
import org.zkoss.zul.Listcell;
import org.zkoss.zul.Listitem;
import org.zkoss.zul.ListitemRenderer;
import org.zkoss.zul.Textbox;
import org.zkoss.zul.Window;

import com.pyramix.domain.coa.Coa_05_Master;
import com.pyramix.domain.gl.GeneralLedger;
import com.pyramix.domain.project.Project;
import com.pyramix.domain.project.ProjectJournal;
import com.pyramix.domain.project.ProjectJournalDebitCredit;
import com.pyramix.domain.user.User;
import com.pyramix.domain.voucher.VoucherSerialNumber;
import com.pyramix.domain.voucher.VoucherStatus;
import com.pyramix.domain.voucher.VoucherType;
import com.pyramix.persistence.project.dao.ProjectDao;
import com.pyramix.persistence.projectjournal.dao.ProjectJournalDao;
import com.pyramix.persistence.user.dao.UserDao;
import com.pyramix.web.common.GFCBaseController;
import com.pyramix.web.serial.SerialNumberGenerator;

public class ProjectJournalController extends GFCBaseController {

	/**
	 * 
	 */
	private static final long serialVersionUID = 4849317134511993894L;
	
	private UserDao userDao;
	private ProjectDao projectDao;
	private ProjectJournalDao projectJournalDao;
	private SerialNumberGenerator serialNumberGenerator;
	
	private Combobox periodCombobox, projectCombobox;
	private Listbox projectJournalListbox;
	
	private User userCreate;
	private LocalDate periodStartDate, periodEndDate;
	private ZoneId zoneId = getZoneId();
	private LocalDate todayDate = getLocalDate(zoneId);
	private List<ProjectJournal> projectJournalList;
	private ListModelList<ProjectJournal> projectJournalListModelList;
	
	/*
	 * ASSUMING User will be able to define starting year - in settings, perhaps
	 * But, to start with, we assume starting year is 2024
	 */
	private static final int START_YEAR = 2024;	
	private static final String PROPERTIES_FILE_PATH="/pyramix/config.properties";
	
	private static final Logger log = Logger.getLogger(ProjectJournalController.class);
	
	public void onCreate$projectJournalPanel(Event event) throws Exception {
		log.info("ProjectJournalPanel created.");

		// login user
		userCreate = getUserDao().findUserByUsername(getLoginUsername());

		// read config
		int selIndex = getConfigSelectedIndex();

		// list periods
		listYearMonthPeriods();
		periodCombobox.setSelectedIndex(selIndex);
		setPeriodStartAndEndDate();
		
		// list active projects
		listActiveProjects();
		projectCombobox.setSelectedIndex(0);
		
		// list projectJournal
		listProjectJournalTransactions();
		
	}

	public void onCheck$defaultCheckbox(Event event) throws Exception {
		int selIndex =	periodCombobox.getSelectedIndex();

		try (InputStream input = new FileInputStream(PROPERTIES_FILE_PATH)) {
            Properties prop = new Properties();

            // load the properties file
            prop.load(input);
			// set the properties value
			prop.setProperty("projectjournal_period_index", String.valueOf(selIndex));
			// save properties to project root folder
			prop.store(new FileOutputStream(PROPERTIES_FILE_PATH), null);
            
			log.info(prop);
			
		} catch (IOException io) {
			io.printStackTrace();
		}
	}
	
	private int getConfigSelectedIndex() {
		String idxStr = "0";
		
		try (InputStream input = new FileInputStream(PROPERTIES_FILE_PATH)) {

            Properties prop = new Properties();

            // load the properties file
            prop.load(input);

            // get the property value and print it out
            idxStr = prop.getProperty("projectjournal_period_index");

		} catch (IOException ex) {
            ex.printStackTrace();
        }

		return Integer.valueOf(idxStr);
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

	private void setPeriodStartAndEndDate() {
		int selPeriod = periodCombobox.getSelectedIndex();

		// selPeriod to month
		Month month = Month.of(selPeriod+1);
		
		periodStartDate = LocalDate.of(START_YEAR, month, 1);		
		periodEndDate = periodStartDate.with(TemporalAdjusters.lastDayOfMonth());
		
		log.info("periodStartDate: "+periodStartDate);
		log.info("periodEndDate: "+periodEndDate);
	}


	private void listActiveProjects() throws Exception {
		List<Project> projectList = getProjectDao().findAllProjects();
		
		Comboitem comboitem;
		for (Project project : projectList) {
			if (project.isActive()) {
				comboitem = new Comboitem();
				comboitem.setLabel(project.getProjectName()
						+"-"+project.getCompanyName());
				comboitem.setValue(project);
				comboitem.setParent(projectCombobox);
			}
		}
	}	
	
	public void onClick$findButton(Event event) throws Exception {
		// init
		// begBalance = null;
		// paymentAmount = BigDecimal.ZERO;
		
		// set start and end date
		setPeriodStartAndEndDate();
				
		// begining balance
		// findBeginingBalance();
		// begBalanceLabel.setValue(begBalanceString);
		
		// re-list
		listProjectJournalTransactions();
		
		// calc ending balance
		// calculateEndingBalance();
		// endBalanceLabel.setValue(endBalanceString);
	}	
	
	private void listProjectJournalTransactions() throws Exception {
		Project selProject =
				projectCombobox.getSelectedItem().getValue();

		projectJournalList = 
				getProjectJournalDao().findAllProjectJournalsByDate(selProject, 
						asDate(periodStartDate, zoneId), asDate(periodEndDate, zoneId));
		
		projectJournalListModelList =
				new ListModelList<ProjectJournal>(projectJournalList);
		
		projectJournalListbox.setModel(projectJournalListModelList);
		projectJournalListbox.setItemRenderer(getProjectJournalListitemRenderer());
	}	
	
	private ListitemRenderer<ProjectJournal> getProjectJournalListitemRenderer() {
		
		return new ListitemRenderer<ProjectJournal>() {
			
			@Override
			public void render(Listitem item, ProjectJournal projectJournal, int index) throws Exception {
				Listcell lc;
				
				// Tgl.Jurnal
				lc = initTransactionDate(new Listcell(), projectJournal);
				lc.setParent(item);
				
				// No.Voucher
				lc = initVoucherNumber(new Listcell(), projectJournal);
				lc.setParent(item);
				
				// Nominal(Rp.)
				lc = initTheSumOf(new Listcell(), projectJournal);
				lc.setParent(item);
				
				// COA (DB)
				lc = initDebitCOA(new Listcell(), projectJournal);
				lc.setParent(item);
				
				// COA (CR)
				lc = initCreditCOA(new Listcell(), projectJournal);
				lc.setParent(item);				
				
				// Transaksi Info
				lc = initTransactionDescription(new Listcell(), projectJournal);
				lc.setParent(item);
				
				// Referensi
				lc = initDocumentRef(new Listcell(), projectJournal);
				lc.setParent(item);
				
				// User Create
				lc = initUserCreate(new Listcell(), projectJournal);
				lc.setParent(item);
				
				// Status
				lc = initStatus(new Listcell(), projectJournal);
				lc.setParent(item);

				// edit
				lc = initSaveOrEdit(new Listcell(), projectJournal);
				lc.setParent(item);
				
				// posting
				lc = initPosting(new Listcell(), projectJournal);
				lc.setParent(item);				
			}

			private Listcell initTransactionDate(Listcell listcell, ProjectJournal projectJournal) {
				if (projectJournal.getId().compareTo(Long.MIN_VALUE)==0) {
					// set todayDate according to the 'Period'
					int selPeriod = periodCombobox.getSelectedIndex();
					// selPeriod to month
					Month month = Month.of(selPeriod+1);
					// new
					setupJournalDatebox(listcell, LocalDate.of(START_YEAR, month, 1));					
				} else {
					listcell.setLabel(dateToStringDisplay(asLocalDate(projectJournal.getTransactionDate()),
							getEmphYearMonthShort(), getLocale()));					
				}
				
				return listcell;
			}

			private Listcell initVoucherNumber(Listcell listcell, ProjectJournal projectJournal) {
				if (projectJournal.getId().compareTo(Long.MIN_VALUE)==0) {
					// created later just before saving
				} else {
					listcell.setLabel(projectJournal.getVoucherNumber().getSerialComp());
				}
				
				return listcell;
			}

			private Listcell initTheSumOf(Listcell listcell, ProjectJournal projectJournal) throws Exception {
				if (projectJournal.getId().compareTo(Long.MIN_VALUE)==0) {
					// new
					setupSumOfDecimalbox(listcell, BigDecimal.ZERO);					
				} else {
					listcell.setLabel(toDecimalFormat(projectJournal.getTheSumOf(), getLocale(), "###.###.###,-"));
				}
				
				return listcell;
			}

			private Listcell initDebitCOA(Listcell listcell, ProjectJournal projectJournal) {
				if (projectJournal.getId().compareTo(Long.MIN_VALUE)==0) {
					// new
					setupCoaDebitSelection(listcell, projectJournal);
				} else {
					listcell.setLabel(projectJournal.getDebitMasterCoa().getMasterCoaComp()
							+"-"+projectJournal.getDebitMasterCoa().getMasterCoaName()+" [DB]");
					// listcell.setAttribute("masterCoa", projectJournal.getDebitMasterCoa());
				}
								
				return listcell;
			}

			private Listcell initCreditCOA(Listcell listcell, ProjectJournal projectJournal) {
				if (projectJournal.getId().compareTo(Long.MIN_VALUE)==0) {
					// new
					setupCoaCreditSelection(listcell, projectJournal);
				} else {
					listcell.setLabel(projectJournal.getCreditMasterCoa().getMasterCoaComp()
							+"-"+projectJournal.getCreditMasterCoa().getMasterCoaName()+" [CR]");
					// listcell.setAttribute("masterCoa", projectJournal.getCreditMasterCoa());
				}
								
				return listcell;
			}

			private Listcell initTransactionDescription(Listcell listcell, ProjectJournal projectJournal) {
				if (projectJournal.getId().compareTo(Long.MIN_VALUE)==0) {
					// new
					setupTextbox(listcell, "");
				} else {
					listcell.setLabel(projectJournal.getTransactionDescription());
				}

				return listcell;
			}

			private Listcell initDocumentRef(Listcell listcell, ProjectJournal projectJournal) {
				if (projectJournal.getId().compareTo(Long.MIN_VALUE)==0) {
					// new
					setupTextbox(listcell, "");
				} else {
					listcell.setLabel(projectJournal.getDocumentRef());
				}

				return listcell;
			}

			private Listcell initUserCreate(Listcell listcell, ProjectJournal projectJournal) throws Exception {
				if (projectJournal.getId().compareTo(Long.MIN_VALUE)==0) {
					// do nothing
				} else {
					ProjectJournal projectJournalUserCreateByProxy =
							getProjectJournalDao().findProjectJournalUserCreateByProxy(projectJournal.getId());
					listcell.setLabel(
							projectJournalUserCreateByProxy.getUserCreate().getReal_name());
					listcell.setValue(projectJournalUserCreateByProxy.getUserCreate());
				}

				return listcell;
			}

			private Listcell initSaveOrEdit(Listcell listcell, ProjectJournal projectJournal) {
				if (projectJournal.getId().compareTo(Long.MIN_VALUE)==0) {
					Button button = new Button();
					button.setLabel("Save");
					button.setParent(listcell);
					button.addEventListener(Events.ON_CLICK, onSaveButtonClicked(listcell, projectJournal));
				} else if(projectJournal.getVoucherStatus().compareTo(VoucherStatus.Posted)==0) {
					// do nothing
				} else {
					Button button = new Button();
					button.setLabel("Modify");
					button.setParent(listcell);
					button.addEventListener(Events.ON_CLICK, onModifyButtonClicked(listcell, projectJournal));
				}
				return listcell;
			}

			// ************************** NEW ****************************
			
			private EventListener<Event> onSaveButtonClicked(Listcell listcell,
					ProjectJournal projectJournal) {
				
				return new EventListener<Event>() {
					
					@Override
					public void onEvent(Event event) throws Exception {
						log.info("SaveButton clicked.");
						ProjectJournal projectJournalData =
								getProjectJournalData(listcell, projectJournal);
						log.info(projectJournalData.toString());
						// save
						getProjectJournalDao().save(projectJournalData);
						// re-list
						listProjectJournalTransactions();
					}

				};
			}

			private ProjectJournal getProjectJournalData(Listcell listcell, ProjectJournal projectJournal) throws Exception {
				Datebox datebox =
						(Datebox) listcell.getParent().getChildren().get(0).getFirstChild();
				Decimalbox decimalbox =
						(Decimalbox) listcell.getParent().getChildren().get(2).getFirstChild();
				Label dbLabel =
						(Label) listcell.getParent().getChildren().get(3).getFirstChild();
				Label crLabel =
						(Label) listcell.getParent().getChildren().get(4).getFirstChild();
				Textbox textboxTransInfo =
						(Textbox) listcell.getParent().getChildren().get(5).getFirstChild();
				Textbox textboxRef =
						(Textbox) listcell.getParent().getChildren().get(6).getFirstChild();				
				// voucher type is
				VoucherType voucherType = VoucherType.PROJECT;
				// masterCoa
				Coa_05_Master debitCoa = (Coa_05_Master) dbLabel.getAttribute("masterCoa");
				Coa_05_Master creditCoa = (Coa_05_Master) crLabel.getAttribute("masterCoa");
				// project
				Project project = projectCombobox.getSelectedItem().getValue();
				// set
				projectJournal.setProject(project);
				projectJournal.setTransactionDate(datebox.getValue());
				projectJournal.setTheSumOf(decimalbox.getValue());
				projectJournal.setDebitMasterCoa(debitCoa);
				projectJournal.setCreditMasterCoa(creditCoa);
				projectJournal.setTransactionDescription(textboxTransInfo.getValue());
				projectJournal.setDocumentRef(textboxRef.getValue());
				// set new voucherSerialNumber and new list for the DbCr list
				if (projectJournal.getId().compareTo(Long.MIN_VALUE)==0) {
					projectJournal.setVoucherNumber(getVoucherSerialNumber(voucherType, datebox.getValue()));
					projectJournal.setCreateDate(asDate(todayDate, zoneId));
					// create projectJournalDebitCredit list
					projectJournal.setProjectJournalDebitCredits(getProjectJournalDebitCredits(projectJournal, debitCoa, creditCoa));
				} else {
					log.info("update projectJournalDebitCreditList");
					projectJournal.setProjectJournalDebitCredits(updateProjectJournalDebitCredits(projectJournal));
				}
				projectJournal.setVoucherStatus(VoucherStatus.Submitted);
				projectJournal.setUserCreate(userCreate);
				LocalDateTime currentDateTime = getLocalDateTime(zoneId);
				projectJournal.setLastModified(asDateTime(currentDateTime, zoneId));
				
				return projectJournal;
			}

			private List<ProjectJournalDebitCredit> getProjectJournalDebitCredits(ProjectJournal projectJournal,
					Coa_05_Master masterCoaToDebit, Coa_05_Master masterCoaToCredit) {
				List<ProjectJournalDebitCredit> projectJournalDebitCredits = new ArrayList<ProjectJournalDebitCredit>();
				LocalDateTime currentDateTime = getLocalDateTime(zoneId);
				
				ProjectJournalDebitCredit dbcr;

				// DB account
				dbcr = new ProjectJournalDebitCredit();
				dbcr.setCreateDate(asDate(todayDate, zoneId));
				dbcr.setCreditAmount(BigDecimal.ZERO);
				dbcr.setDebitAmount(projectJournal.getTheSumOf());
				dbcr.setDbcrDescription(projectJournal.getTransactionDescription());
				dbcr.setLastModified(asDateTime(currentDateTime, zoneId));
				dbcr.setMasterCoa(masterCoaToDebit);				
				
				// add to list
				projectJournalDebitCredits.add(dbcr);

				// CR account
				dbcr = new ProjectJournalDebitCredit();
				dbcr.setCreateDate(asDate(todayDate, zoneId));				
				dbcr.setCreditAmount(projectJournal.getTheSumOf());
				dbcr.setDebitAmount(BigDecimal.ZERO);
				dbcr.setDbcrDescription(projectJournal.getTransactionDescription());
				dbcr.setLastModified(asDateTime(currentDateTime, zoneId));
				dbcr.setMasterCoa(masterCoaToCredit);

				// add to list
				projectJournalDebitCredits.add(dbcr);

				return projectJournalDebitCredits;
			}

			private List<ProjectJournalDebitCredit> updateProjectJournalDebitCredits(ProjectJournal projectJournal) {
				List<ProjectJournalDebitCredit> projectJournalDebitCredits = projectJournal.getProjectJournalDebitCredits();
				LocalDateTime currentDateTime = getLocalDateTime(zoneId);
				ProjectJournalDebitCredit dbcr;
				
				// 1st Row is DB account
				dbcr = projectJournalDebitCredits.get(0);
				dbcr.setDbcrDescription(projectJournal.getTransactionDescription());
				dbcr.setCreditAmount(BigDecimal.ZERO);
				dbcr.setDebitAmount(projectJournal.getTheSumOf()); // <-- update DB
				dbcr.setLastModified(asDateTime(currentDateTime, zoneId));				
				dbcr.setMasterCoa(projectJournal.getDebitMasterCoa());
				
				// 2nd Row is CR account
				dbcr = projectJournalDebitCredits.get(1);
				dbcr.setDbcrDescription(projectJournal.getTransactionDescription());
				dbcr.setCreditAmount(projectJournal.getTheSumOf()); // <-- update CR
				dbcr.setDebitAmount(BigDecimal.ZERO);
				dbcr.setLastModified(asDateTime(currentDateTime, zoneId));
				dbcr.setMasterCoa(projectJournal.getCreditMasterCoa());
				 
				return projectJournalDebitCredits;
			}

			// ************************** EXISTING ****************************

			private EventListener<Event> onModifyButtonClicked(Listcell listcell,
					ProjectJournal projectJournal) {
				
				return new EventListener<Event>() {
					
					@Override
					public void onEvent(Event event) throws Exception {
						Button button = (Button) event.getTarget();
						
						if (button.getLabel().compareTo("Modify")==0) {
							// re-create components for data entry - allow user to edit
							modifyProjectJournalData(listcell, projectJournal);
							// change button to 'save'
							button.setLabel("Save");							
						} else {
							ProjectJournal projectJournalModData =
									getProjectJournalData(listcell, projectJournal);
							log.info(projectJournalModData.toString());
							// update
							getProjectJournalDao().update(projectJournalModData);
							// re-list
							listProjectJournalTransactions();
							// change button to 'modify'
							button.setLabel("Modify");
						}
					}

					private void modifyProjectJournalData(Listcell listcell, ProjectJournal projectJournal) {
						// re-create datebox for data entry
						Listcell lc0 = (Listcell) listcell.getParent().getChildren().get(0);
						lc0.setLabel(null);
						setupJournalDatebox(lc0, asLocalDate(projectJournal.getTransactionDate()));
		
						// re-create nominal decimalbox for data entry
						Listcell lc2 = (Listcell) listcell.getParent().getChildren().get(2);
						lc2.setLabel(null);
						setupSumOfDecimalbox(lc2, projectJournal.getTheSumOf());
						
						// re-create debitCoa for data entry
						Listcell lc3 = (Listcell) listcell.getParent().getChildren().get(3);
						lc3.setLabel(null);
						setupCoaDebitSelection(lc3, projectJournal);
						
						// re-create creditCoa for data entry
						Listcell lc4 = (Listcell) listcell.getParent().getChildren().get(4);
						lc4.setLabel(null);
						setupCoaCreditSelection(lc4, projectJournal);
						
						// re-create textbox transaksi info for data entry
						Listcell lc5 = (Listcell) listcell.getParent().getChildren().get(5);
						lc5.setLabel(null);
						setupTextbox(lc5, projectJournal.getTransactionDescription());

						// re-create textbox referensi for data entry
						Listcell lc6 = (Listcell) listcell.getParent().getChildren().get(6);
						lc6.setLabel(null);
						setupTextbox(lc6, projectJournal.getDocumentRef());
					}
				};
			}

			private Listcell initStatus(Listcell listcell, ProjectJournal projectJournal) {
				if (projectJournal.getId().compareTo(Long.MIN_VALUE)==0) {
					// default to 'submitted'
					listcell.setLabel(VoucherStatus.Submitted.toString());
				} else {
					listcell.setLabel(projectJournal.getVoucherStatus().toString());
				}
				
				return listcell;
			}

			//***************************** Posting ***********************************
			
			private Listcell initPosting(Listcell listcell, ProjectJournal projectJournal) {
				if (projectJournal.getId().compareTo(Long.MIN_VALUE)==0) {
					
				} else if (projectJournal.getVoucherStatus().compareTo(VoucherStatus.Submitted)==0) {
					Button button = new Button();
					button.setLabel("Post");
					button.setParent(listcell);
					button.addEventListener(Events.ON_CLICK, onPostButtonClicked(listcell, projectJournal));
				}
				return listcell;
			}

			private EventListener<Event> onPostButtonClicked(Listcell listcell,
					ProjectJournal projectJournal) {
				
				return new EventListener<Event>() {
					
					@Override
					public void onEvent(Event event) throws Exception {
						log.info("PostButton clicked.");
						// create glList
						List<GeneralLedger> glList = new ArrayList<GeneralLedger>();
						// create posting voucher serialnumber
						VoucherSerialNumber postingVoucherNumber =
								getVoucherSerialNumber(VoucherType.POSTING_PROJECT, projectJournal.getTransactionDate());
						// create gl objects
						for(ProjectJournalDebitCredit dbcr : projectJournal.getProjectJournalDebitCredits()) {
							GeneralLedger gl = new GeneralLedger();
							
							gl.setMasterCoa(dbcr.getMasterCoa());
							gl.setPostingDate(projectJournal.getTransactionDate());
							gl.setPostingVoucherNumber(postingVoucherNumber);
							gl.setCreditAmount(dbcr.getCreditAmount());
							gl.setDebitAmount(dbcr.getDebitAmount());
							gl.setDbcrDescription(dbcr.getDbcrDescription());
							gl.setTransactionDescription(projectJournal.getTransactionDescription());
							gl.setDocumentRef(projectJournal.getDocumentRef());
							gl.setTransactionDate(projectJournal.getTransactionDate());
							gl.setVoucherType(VoucherType.CREDITCARD);
							gl.setVoucherNumber(projectJournal.getVoucherNumber());
							gl.setCreateDate(asDate(todayDate, zoneId));
							LocalDateTime currentDateTime = getLocalDateTime(zoneId);
							gl.setLastModified(asDateTime(currentDateTime, zoneId));
							
							glList.add(gl);							
						}
						// set
						projectJournal.setGeneralLedgers(glList);
						// update projectJournal status
						projectJournal.setVoucherStatus(VoucherStatus.Posted);
						projectJournal.setPostingVoucherNumber(postingVoucherNumber);
						projectJournal.setPostingDate(asDate(todayDate, zoneId));
						// update
						getProjectJournalDao().update(projectJournal);
						// re-list
						listProjectJournalTransactions();
					}
				};
			}
		};
	}

	protected void setupJournalDatebox(Listcell listcell, LocalDate localDate) {
		Datebox datebox = new Datebox();
		datebox.setWidth("130px");
		datebox.setLocale(getLocale());
		datebox.setValue(asDate(localDate, zoneId));
		datebox.setParent(listcell);		
	}

	protected void setupSumOfDecimalbox(Listcell listcell, BigDecimal amount) {
		Decimalbox decimalbox = new Decimalbox();
		decimalbox.setWidth("110px");
		decimalbox.setLocale(getLocale());
		decimalbox.setValue(amount);
		decimalbox.setParent(listcell);
	}
	
	protected void setupTextbox(Listcell listcell, String description) {
		Textbox textbox = new Textbox();
		textbox.setWidth("180px");
		textbox.setValue(description);
		textbox.setParent(listcell);
	}	
	
	private void setupCoaDebitSelection(Listcell listcell, ProjectJournal projectJournal) {
		Label debitLabel = new Label();
		if (projectJournal.getId().compareTo(Long.MIN_VALUE)==0) {
			debitLabel.setValue("COA to [DB]");
		} else {
			debitLabel.setValue(projectJournal.getDebitMasterCoa().getMasterCoaComp()
					+"-"+projectJournal.getDebitMasterCoa().getMasterCoaName()+" [DB]");
			debitLabel.setAttribute("masterCoa", projectJournal.getDebitMasterCoa());
		}
		debitLabel.setParent(listcell);
		debitLabel.addEventListener(Events.ON_CLICK, onCoaLabelClick(debitLabel));
	}
	
	private void setupCoaCreditSelection(Listcell listcell, ProjectJournal projectJournal) {
		Label creditLabel = new Label();
		if (projectJournal.getId().compareTo(Long.MIN_VALUE)==0) {
			creditLabel.setValue("COA to [CR]");
		} else {
			creditLabel.setValue(projectJournal.getCreditMasterCoa().getMasterCoaComp()
					+"-"+projectJournal.getCreditMasterCoa().getMasterCoaName()+" [CR]");
			creditLabel.setAttribute("masterCoa", projectJournal.getCreditMasterCoa());
		}
		creditLabel.setParent(listcell);
		creditLabel.addEventListener(Events.ON_CLICK, onCoaLabelClick(creditLabel));
	}	
	
	private EventListener<Event> onCoaLabelClick(Label label) {

		return new EventListener<Event>() {
			
			@Override
			public void onEvent(Event event) throws Exception {
				log.info("COA Label Clicked.");
				Window window = 
						(Window) Executions.createComponents("~./secure/coa/Coa_05_MasterDialog.zul", 
								null, null);
				window.doModal();
				window.addEventListener(Events.ON_SELECT, new EventListener<Event>() {

					@Override
					public void onEvent(Event event) throws Exception {
						Coa_05_Master masterCoa = (Coa_05_Master) event.getData();
						log.info("Received Master COA: "+masterCoa.toString());
						
						label.setValue(masterCoa.getMasterCoaComp()
								+"-"+masterCoa.getMasterCoaName());
						label.setAttribute("masterCoa", masterCoa);
					}
				});
			}
		};
	}

	
	private VoucherSerialNumber getVoucherSerialNumber(VoucherType voucherType, Date currentDate) throws Exception {
		int serialNum = getSerialNumberGenerator().getSerialNumber(voucherType, currentDate);
		
		VoucherSerialNumber voucherSerNum = new VoucherSerialNumber();
		voucherSerNum.setVoucherType(voucherType);
		voucherSerNum.setSerialDate(currentDate);
		voucherSerNum.setSerialNo(serialNum);
		voucherSerNum.setSerialComp(formatSerialComp(voucherType.toCode(voucherType.getValue()), currentDate, serialNum));
		voucherSerNum.setCreateDate(asDate(todayDate, zoneId));
		LocalDateTime localDateTime = getLocalDateTime(zoneId);
		voucherSerNum.setLastModified(asDateTime(localDateTime, zoneId));
		
		return voucherSerNum;
	}
	
	public void onClick$newProjectJournalButton(Event event) throws Exception {
		// add a new ProjectJournal object
		projectJournalListModelList.add(0, new ProjectJournal());
	}
	
	public ProjectJournalDao getProjectJournalDao() {
		return projectJournalDao;
	}

	public void setProjectJournalDao(ProjectJournalDao projectJournalDao) {
		this.projectJournalDao = projectJournalDao;
	}

	public ProjectDao getProjectDao() {
		return projectDao;
	}

	public void setProjectDao(ProjectDao projectDao) {
		this.projectDao = projectDao;
	}

	public UserDao getUserDao() {
		return userDao;
	}

	public void setUserDao(UserDao userDao) {
		this.userDao = userDao;
	}

	public SerialNumberGenerator getSerialNumberGenerator() {
		return serialNumberGenerator;
	}

	public void setSerialNumberGenerator(SerialNumberGenerator serialNumberGenerator) {
		this.serialNumberGenerator = serialNumberGenerator;
	}

}
