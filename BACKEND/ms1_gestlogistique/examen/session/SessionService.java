package com.groupeisi.ms1_gestschool.examen.session;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;

public interface SessionService {

    SessionDtoResponse create(SessionDtoRequest request);

    SessionDtoResponse update(String id, SessionDtoRequest request);

    SessionDtoResponse findById(String id);

    List<SessionDtoResponse> findAll();
    Page<SessionDtoResponse> getAllCompostionPaged(Pageable pageable);
    void delete(String id);
}