package com.scalable.inventory.type.json;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.*;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@JsonInclude(JsonInclude.Include.NON_NULL)
@ToString(includeFieldNames = true)
abstract class BaseJSON {
    private String username;
    private String order_id;
    private String item_name;
    private long amount;
}
