package com.groupeisi.ms1_gestschool.examen.session;

import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class SessionServiceImpl implements SessionService {

    private final SessionRepository repository;
    private final SessionMapper mapper;

    @Override
    public SessionDtoResponse create(SessionDtoRequest request) {
        SessionEntity entity = mapper.toEntity(request);
        return mapper.toDto(repository.save(entity));
    }

    @Override
    public SessionDtoResponse update(String id, SessionDtoRequest request) {
        SessionEntity entity = repository.findById(id)
                .orElseThrow(() -> new RuntimeException("Composition introuvable"));

        mapper.updateEntityFromDto(request, entity);
        return mapper.toDto(repository.save(entity));
    }

    @Override
    public SessionDtoResponse findById(String id) {
        return repository.findById(id)
                .map(mapper::toDto)
                .orElseThrow(() -> new RuntimeException("Composition introuvable"));
    }

    @Override
    public List<SessionDtoResponse> findAll() {
        return repository.findAll()
                .stream()
                .map(mapper::toDto)
                .collect(Collectors.toList());
    }

    @Override
    public Page<SessionDtoResponse> getAllCompostionPaged(Pageable pageable) {
        Page<SessionEntity> eleves = repository.findAll(pageable);

        Page<SessionDtoResponse> response =
                eleves.map(mapper::toDto);

        return response;
    }

    @Override
    public void delete(String id) {
        if (!repository.existsById(id)) {
            throw new RuntimeException("Composition introuvable");
        }
        repository.deleteById(id);
    }
}
