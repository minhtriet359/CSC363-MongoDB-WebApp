package application.model;

import org.springframework.data.mongodb.repository.MongoRepository;

public interface PrescriptionRepository extends MongoRepository<Prescription, Integer> {
    Prescription findByPatientIdAndRxid(int patientId,int Rxid);
}
