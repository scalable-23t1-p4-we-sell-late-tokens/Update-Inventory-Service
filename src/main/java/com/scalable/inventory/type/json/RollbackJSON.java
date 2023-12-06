package com.scalable.inventory.type.json;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@AllArgsConstructor
@Getter
@Setter
@JsonInclude(JsonInclude.Include.NON_NULL)
@ToString(includeFieldNames = true)
public class RollbackJSON extends BaseJSON {
    private String item_name;
    private long amount;
    private String message_response;
}
