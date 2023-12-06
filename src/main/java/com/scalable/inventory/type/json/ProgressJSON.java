package com.scalable.inventory.type.json;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@JsonInclude(JsonInclude.Include.NON_NULL)
@ToString(includeFieldNames = true)
public class ProgressJSON extends BaseJSON {
    private String username;
    private String item_name;
    private long amount;
    private String message_flag;


}