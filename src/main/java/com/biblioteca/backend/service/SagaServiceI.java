package com.biblioteca.backend.service;

import com.biblioteca.backend.model.Saga;
import java.util.List;

public interface SagaServiceI {
    List<Saga> getAllSagas();

    Saga getSagaById(Long id);

    Saga createSaga(Saga saga);

    Saga updateSaga(Long id, Saga sagaDetails);

    void deleteSaga(Long id);
}
