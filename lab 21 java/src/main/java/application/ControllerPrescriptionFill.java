package application;

import application.model.*;
import application.service.SequenceService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import view.PrescriptionView;

import java.sql.*;
import java.text.DecimalFormat;
import java.time.LocalDate;
import java.util.ArrayList;

@Controller
public class ControllerPrescriptionFill {

	@Autowired
	PatientRepository patientRepository;
	@Autowired
	PrescriptionRepository prescriptionRepository;
	@Autowired
	DoctorRepository doctorRepository;
	@Autowired
	DrugRepository drugRepository;
	@Autowired
	PharmacyRepository pharmacyRepository;
	@Autowired
	SequenceService sequence;

	/*
	 * Patient requests form to fill prescription.
	 */
	@GetMapping("/prescription/fill")
	public String getfillForm(Model model) {
		model.addAttribute("prescription", new PrescriptionView());
		return "prescription_fill";
	}

	// process data from prescription_fill form
	@PostMapping("/prescription/fill")
	public String processFillForm(PrescriptionView pv, Model model) {
		//validate pharmacy name and address
		Pharmacy pharmacy=pharmacyRepository.findByNameAndAddress(pv.getPharmacyName(),pv.getPharmacyAddress());
		if(pharmacy==null){
			model.addAttribute("message", "Pharmacy not found.");
			model.addAttribute("prescription", pv);
			return "prescription_fill";
		}
		//find the patient information from last name in fill form
		Patient patient=patientRepository.findByLastName(pv.getPatientLastName());
		if(patient==null){
			model.addAttribute("message", "Patient not found.");
			model.addAttribute("prescription", pv);
			return "prescription_fill";
		}
		//find the prescription from patientID and RXid
		int patientId=patient.getId();
		Prescription prescription=prescriptionRepository.findByPatientIdAndRxid(patientId,pv.getRxid());
		if(prescription==null){
			model.addAttribute("message", "Prescription not found.");
			model.addAttribute("prescription", pv);
			return "prescription_fill";
		}
		//check to see if pharmacy carry the drug on prescription
		boolean match=false;
		double drugPrice=0.0;
		ArrayList<Pharmacy.DrugCost> drugCosts=pharmacy.getDrugCosts();
		for(Pharmacy.DrugCost drugCost: drugCosts){
			if(drugCost.getDrugName().equals(prescription.getDrugName())){
				match=true;
				drugPrice=drugCost.getCost();
				break;
			}
		}
		if(!match){
			model.addAttribute("message", "Pharmacy does not carry drug required.");
			model.addAttribute("prescription", pv);
			return "prescription_fill";
		}
		//check to see if the number of refills is exceeded, first fill does not count as refill
		if(prescription.getRefills()<=0){
			model.addAttribute("message", "No more refills.");
			model.addAttribute("prescription", pv);
			return "prescription_fill";
		}

		//update prescription
		if(!prescription.getFills().isEmpty()){
			prescription.setRefills(prescription.getRefills()-1);
		}
		Prescription.FillRequest fill=new Prescription.FillRequest();
		fill.setDateFilled(String.valueOf(Date.valueOf(LocalDate.now())));
		fill.setPharmacyID(pharmacy.getId());
		fill.setCost(String.valueOf(drugPrice*prescription.getQuantity()));
		prescription.getFills().add(fill);
		prescriptionRepository.save(prescription);

		// copy data from model to view
		pv.setDrugName(prescription.getDrugName());
		pv.setQuantity(prescription.getQuantity());
		pv.setPatientId(prescription.getPatientId());
		pv.setPatientFirstName(patient.getFirstName());
		pv.setDoctorId(prescription.getDoctorId());
		pv.setDoctorLastName(doctorRepository.findById(prescription.getDoctorId()).getLastName());
		pv.setDoctorFirstName(doctorRepository.findById(prescription.getDoctorId()).getFirstName());
		pv.setPharmacyID(pharmacy.getId());
		pv.setPharmacyPhone(pharmacy.getPhone());
		pv.setDateFilled(String.valueOf(Date.valueOf(LocalDate.now())));
		pv.setCost(String.valueOf(drugPrice*prescription.getQuantity()));
		pv.setRefills(prescription.getRefills());

		model.addAttribute("message", "Prescription has been filled.");
		model.addAttribute("prescription", pv);
		return "prescription_show";
	}
}