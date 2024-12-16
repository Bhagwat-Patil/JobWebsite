package com.jobwebsite.Service;

import com.jobwebsite.Entity.Placement;
import java.util.List;

public interface PlacementService {
    Placement createPlacement(Placement placement);
    Placement getPlacementById(Long id);
    List<Placement> getAllPlacements();
    Placement updatePlacement(Long id, Placement placement);
    void deletePlacement(Long id);
}