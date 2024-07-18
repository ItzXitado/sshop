package io.github.xitadoo.sshop.models;


import lombok.*;

import java.util.List;
import java.util.UUID;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class User {

    private UUID playerId;
    private List<History> playerHistory;
}
