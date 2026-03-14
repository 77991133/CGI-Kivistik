package ee.kivistik.backend.dto;

import ee.kivistik.backend.entity.TableEntity;

import java.util.List;

public class TableRecommendationResponse {

    public List<TableEntity> tables;
    public boolean combined;
    public List<Integer> bookedTableIds;

}