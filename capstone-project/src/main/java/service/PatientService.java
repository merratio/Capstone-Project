package service;

import entity.Patient;
import org.springframework.stereotype.Service;
import repository.PatientRepository;

@Service
public class PatientService {

    private final PatientRepository repo;
    private final BlockchainService blockchainService;

    public PatientService(PatientRepository repo,
                          BlockchainService blockchainService) {
        this.repo = repo;
        this.blockchainService = blockchainService;
    }

    public void createPatient(PatientDto dto) {

        Patient patient = map(dto);

        repo.save(patient);

        String hash = HashUtil.generateHash(patient);

        blockchainService.storeHash(patient.getId(), hash);
    }

    public void updatePatient(String id, PatientDto dto) {

        Patient patient = repo.findById(id)
                .orElseThrow();

        patient.setDiagnosis(dto.getDiagnosis());
        patient.setLastUpdated(LocalDateTime.now());

        repo.save(patient);

        String hash = HashUtil.generateHash(patient);

        blockchainService.updateHash(id, hash);
    }
}
