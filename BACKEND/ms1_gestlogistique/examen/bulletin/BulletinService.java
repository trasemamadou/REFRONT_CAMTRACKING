package com.groupeisi.ms1_gestschool.examen.bulletin;

import java.util.List;

public interface BulletinService {
    BulletinDtoResponse createBulletin(BulletinDtoRequest request);
    BulletinDtoResponse getBulletinById(String id);
    List<BulletinDtoResponse> getAllBulletins();
    BulletinDtoResponse updateBulletin(String id, BulletinDtoRequest request);
    void deleteBulletin(String id);
    List<BulletinDtoResponse> getBulletinsByEleve(String eleveId);
    List<BulletinDtoResponse> getBulletinsByClasseId(String sessionclasseId);
}