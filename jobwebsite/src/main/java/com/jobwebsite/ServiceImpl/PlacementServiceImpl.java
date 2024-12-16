package com.jobwebsite.ServiceImpl;

import com.jobwebsite.Entity.Placement;
import com.jobwebsite.Repository.PlacementRepository;
import com.jobwebsite.Service.PlacementService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import java.util.List;

@Service
public class PlacementServiceImpl implements PlacementService {

    private static final Logger log = LoggerFactory.getLogger(PlacementServiceImpl.class);

    private final PlacementRepository placementRepository;

    public PlacementServiceImpl(PlacementRepository placementRepository) {
        this.placementRepository = placementRepository;
    }

    @Override
    public Placement createPlacement(Placement placement) {
        log.info("Attempting to create a new Placement: {}", placement);
        try {
            Placement savedPlacement = placementRepository.save(placement);
            log.info("Placement created successfully with ID: {}", savedPlacement.getId());
            return savedPlacement;
        } catch (Exception e) {
            log.error("Error occurred while creating Placement: {}", e.getMessage());
            throw e;
        }
    }

    @Override
    public Placement getPlacementById(Long id) {
        log.info("Fetching Placement by ID: {}", id);
        return placementRepository.findById(id)
                .orElseThrow(() -> {
                    log.error("Placement not found with ID: {}", id);
                    return new RuntimeException("Placement not found with ID: " + id);
                });
    }

    @Override
    public List<Placement> getAllPlacements() {
        log.info("Fetching all Placements...");
        try {
            List<Placement> placements = placementRepository.findAll();
            log.info("Successfully fetched {} Placements.", placements.size());
            return placements;
        } catch (Exception e) {
            log.error("Error occurred while fetching Placements: {}", e.getMessage());
            throw e;
        }
    }

    @Override
    public Placement updatePlacement(Long id, Placement placement) {
        log.info("Attempting to update Placement with ID: {}", id);
        try {
            Placement existingPlacement = getPlacementById(id);
            existingPlacement.setText(placement.getText());
            existingPlacement.setHyperlink(placement.getHyperlink());
            Placement updatedPlacement = placementRepository.save(existingPlacement);
            log.info("Placement updated successfully with ID: {}", updatedPlacement.getId());
            return updatedPlacement;
        } catch (Exception e) {
            log.error("Error occurred while updating Placement with ID: {}: {}", id, e.getMessage());
            throw e;
        }
    }

    @Override
    public void deletePlacement(Long id) {
        log.info("Attempting to delete Placement with ID: {}", id);
        try {
            Placement placement = getPlacementById(id);
            placementRepository.delete(placement);
            log.info("Placement deleted successfully with ID: {}", id);
        } catch (Exception e) {
            log.error("Error occurred while deleting Placement with ID: {}: {}", id, e.getMessage());
            throw e;
        }
    }
}
