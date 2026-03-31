package service;

import dto.PatientDto;
import entity.Patient;
import exception.DataIntegrityException;
import exception.ResourceNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import repository.PatientRepository;
import util.HashUtil;

import java.time.LocalDateTime;
import java.util.List;

@Service
public class PatientService {

    private final PatientRepository repo;
    private final BlockchainService blockchainService;

    public PatientService(PatientRepository repo,
                          BlockchainService blockchainService) {
        this.repo = repo;
        this.blockchainService = blockchainService;
    }

    @Transactional
    public void createPatient(PatientDto dto) {
        Patient patient = map(dto);
        repo.save(patient);

        String hash = HashUtil.generateHash(patient);
        blockchainService.storeHash(patient.getId(), hash);
    }

    @Transactional
    public void updatePatient(String id, PatientDto dto) {
        Patient patient = repo.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Patient not found: " + id));

        patient.setName(dto.getName());
        patient.setDiagnosis(dto.getDiagnosis());
        patient.setLastUpdated(LocalDateTime.now());

        repo.save(patient);

        String hash = HashUtil.generateHash(patient);
        blockchainService.updateHash(id, hash);
    }

    @Transactional(readOnly = true)
    public PatientDto getPatient(String id) {
        Patient patient = repo.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Patient not found: " + id));

        verifyIntegrity(patient);

        return toDto(patient);
    }

    @Transactional(readOnly = true)
    public List<PatientDto> getAllPatients() {
        return repo.findAll()
                .stream()
                .map(this::toDto)
                .toList();
    }

    private void verifyIntegrity(Patient patient) {
        String dbHash = HashUtil.generateHash(patient);
        String chainHash = blockchainService.getHash(patient.getId());

        if (!dbHash.equals(chainHash)) {
            throw new DataIntegrityException(
                    "Integrity check failed for patient: " + patient.getId()
                            + " — database record does not match blockchain hash");
        }
    }

    private Patient map(PatientDto dto) {
        return new Patient(
                dto.getId(),
                dto.getName(),
                dto.getDiagnosis(),
                LocalDateTime.now()
        );
    }

    private PatientDto toDto(Patient patient) {
        return new PatientDto(
                patient.getId(),
                patient.getName(),
                patient.getDiagnosis()
        );
    }
}