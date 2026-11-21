
package tn.esprit.exchangeservice.dto;

public class CreateSecureExchangeRequest {

    private String receiverKeycloakId;
    private Long skillId;

    public String getReceiverKeycloakId() {
        return receiverKeycloakId;
    }

    public void setReceiverKeycloakId(String receiverKeycloakId) {
        this.receiverKeycloakId = receiverKeycloakId;
    }

    public Long getSkillId() {
        return skillId;
    }

    public void setSkillId(Long skillId) {
        this.skillId = skillId;
    }
}
