package application;

import application.model.*;
import application.service.*;
import view.*;

import application.model.Patient;
import application.model.PatientRepository;
import application.service.SequenceService;
import org.springframework.beans.factory.annotation.Autowired;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;

import view.PatientView;




/*
 * Controller class for patient interactions.
 *   register as a new patient.
 *   update patient profile.
 */
@Controller
public class ControllerPatientUpdate {

	@Autowired
	PatientRepository patientRepository;
	@Autowired
	DoctorRepository doctorRepository;
	@Autowired
	SequenceService sequence;

	/*
	 *  Display patient profile for patient id.
	 */
	@GetMapping("/patient/edit/{id}")
	public String getUpdateForm(@PathVariable int id, Model model) {
		Patient p = patientRepository.findById(id);
		if (p != null) {
			PatientView pv = new PatientView();
			pv.setId(id);
			pv.setSsn(p.getSsn());
			pv.setFirstName(p.getFirstName());
			pv.setLastName(p.getLastName());
			pv.setBirthdate(p.getBirthdate());
			pv.setStreet(p.getStreet());
			pv.setCity(p.getCity());
			pv.setState(p.getState());
			pv.setZipcode(p.getZipcode());
			pv.setPrimaryName(p.getPrimaryName());
			model.addAttribute("patient", pv);
			return "patient_edit";
		} else {
			model.addAttribute("message", "Patient not found.");
			return "patient_get";
		}

	}//end getUpdateForm


	/*
	 * Process changes from patient_edit form
	 *  Primary doctor, street, city, state, zip can be changed
	 *  ssn, patient id, name, birthdate, ssn are read only in template.
	 */
	@PostMapping("/patient/edit")
	public String updatePatient(PatientView pv, Model model) {
		Patient p = patientRepository.findById(pv.getId());
		p.setPrimaryName(pv.getPrimaryName());
		p.setStreet(pv.getStreet());
		p.setCity(pv.getCity());
		p.setState(pv.getState());
		p.setZipcode(pv.getZipcode());
		patientRepository.save(p);
		model.addAttribute("message", "Update successful");
		model.addAttribute("patient", pv);
		return "patient_show";
	}// end patient update

}