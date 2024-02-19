package application;


import application.model.Doctor;
import application.model.DoctorRepository;
import application.model.Patient;
import application.model.PatientRepository;
import application.service.SequenceService;
import org.springframework.beans.factory.annotation.Autowired;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;

import view.*;

/*
 * Controller class for patient interactions.
 *   register as a new patient.
 *   update patient profile.
 */
@Controller
public class ControllerPatientCreate {

	@Autowired
	PatientRepository patientRepository;
	@Autowired
	DoctorRepository doctorRepository;
	@Autowired
	SequenceService sequence;

	/*
	 * Request blank patient registration form.
	 */
	@GetMapping("/patient/new")
	public String getNewPatientForm(Model model) {
		// return blank form for new patient registration
		model.addAttribute("patient", new PatientView());
		return "patient_register";
	}

	/*
	 * Process data from the patient_register form
	 */
	@PostMapping("/patient/new")
	public String createPatient(PatientView p, Model model) {
		// get the next unique id for patient.
		int id = sequence.getNextSequence("PATIENT_SEQUENCE");
		// create a model.patient instance
		// copy data from PatientView to model
		Patient patientM = new Patient();
		patientM.setId(id);
		patientM.setSsn(p.getSsn());
		patientM.setFirstName(p.getFirstName());
		patientM.setLastName(p.getLastName());
		patientM.setBirthdate(p.getBirthdate());
		patientM.setStreet(p.getStreet());
		patientM.setCity(p.getCity());
		patientM.setState(p.getState());
		patientM.setZipcode(p.getZipcode());
		patientM.setPrimaryName(p.getPrimaryName());
		//check doctor last name
		Doctor d1 = doctorRepository.findByLastName(p.getPrimaryName());
		if (d1 != null) {
			patientM.setPrimaryName(p.getPrimaryName());
		} else {
			model.addAttribute("message", "Doctor not found.");
			model.addAttribute("patient", p);
			return "patient_register";
		}
		//check ssn

		p.setId(id);
		patientRepository.insert(patientM);

		// display patient data and the generated patient ID,  and success message
		model.addAttribute("message", "Registration successful.");
		model.addAttribute("patient", p);
		return "patient_show";
	}//end create patient

	/*
	 * Request blank form to search for patient by id and name
	 */
	@GetMapping("/patient/edit")
	public String getSearchForm(Model model) {
		model.addAttribute("patient", new PatientView());
		return "patient_get";
	}

	/*
	 * Perform search for patient by patient id and name.
	 */
	@PostMapping("/patient/show")
	public String showPatient(PatientView p, Model model) {
		//search for patient by id and name
		// retrieve patient using the id, last_name entered by user
		Patient patient = patientRepository.findByIdAndLastName(p.getId(), p.getLastName());
		if (patient != null) {
			// copy data from model to view
			p.setId(patient.getId());
			p.setSsn(patient.getSsn());
			p.setLastName(patient.getLastName());
			p.setFirstName(patient.getFirstName());
			p.setBirthdate(patient.getBirthdate());
			p.setStreet(patient.getStreet());
			p.setCity(patient.getCity());
			p.setState(patient.getState());
			p.setZipcode(patient.getZipcode());
			p.setPrimaryName(patient.getPrimaryName());
			model.addAttribute("message", "Search successful.");
			model.addAttribute("patient", p);
			return "patient_show";

		} else {
			model.addAttribute("message", "Patient not found.");
			model.addAttribute("patient", p);
			return "patient_get";
		}
	}//end showPatient
	

}
