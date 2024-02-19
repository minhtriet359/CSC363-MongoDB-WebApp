package application;

import java.sql.*;
import java.time.LocalDate;
import java.util.Random;

import application.model.*;
import application.service.SequenceService;
import org.springframework.beans.factory.annotation.Autowired;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;

import view.*;

import javax.xml.transform.Result;

@Controller
public class ControllerPrescriptionCreate {

	@Autowired
	PatientRepository patientRepository;
	@Autowired
	PrescriptionRepository prescriptionRepository;
	@Autowired
	DoctorRepository doctorRepository;
	@Autowired
	DrugRepository drugRepository;
	@Autowired
	SequenceService sequence;
	
	/*
	 * Doctor requests blank form for new prescription.
	 */
	@GetMapping("/prescription/new")
	public String getPrescriptionForm(Model model) {
		model.addAttribute("prescription", new PrescriptionView());
		return "prescription_create";
	}
	// process data entered on prescription_create form
	@PostMapping("/prescription")
	public String createPrescription(PrescriptionView pv, Model model) {
		Doctor d = doctorRepository.findByIdAndFirstNameAndLastName(pv.getDoctorId(),pv.getDoctorFirstName(),pv.getDoctorLastName());
		Patient p = patientRepository.findByIdAndFirstNameAndLastName(pv.getPatientId(),pv.getPatientFirstName(),pv.getPatientLastName());
		Drug drug = drugRepository.findByName(pv.getDrugName());

		if(d==null){
			model.addAttribute("message", "Doctor not found.");
			model.addAttribute("prescription", pv);
			return "prescription_create";
		}
		if(p==null){
			model.addAttribute("message", "Patient not found.");
			model.addAttribute("prescription", pv);
			return "prescription_create";
		}
		if(drug==null){
			model.addAttribute("message", "Drug not found.");
			model.addAttribute("prescription", pv);
			return "prescription_create";
		}
		// get the next unique id for prescription.
		int id = sequence.getNextSequence("PRESCRIPTION_SEQUENCE");

		// copy data from PatientView to model
		Prescription script = new Prescription();
		script.setDrugName(pv.getDrugName());
		script.setQuantity(pv.getQuantity());
		script.setPatientId(pv.getPatientId());
		script.setDoctorId(pv.getDoctorId());
		script.setDateCreated(String.valueOf(Date.valueOf(LocalDate.now())));
		script.setRefills(pv.getRefills());

		//insert prescription into db
		script.setRxid(id);
		prescriptionRepository.insert(script);

		//display prescription information
		model.addAttribute("message", "Prescription created.");
		model.addAttribute("prescription", pv);
		return "prescription_show";
		}
	}


