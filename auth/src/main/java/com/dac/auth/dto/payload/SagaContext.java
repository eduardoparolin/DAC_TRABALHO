package com.dac.auth.dto.payload;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@AllArgsConstructor
@NoArgsConstructor
@Data
public class SagaContext {
    private String idSaga;
    private Integer step;
    private String status;
}
