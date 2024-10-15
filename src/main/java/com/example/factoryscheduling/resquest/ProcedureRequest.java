package com.example.factoryscheduling.resquest;


import java.time.LocalDateTime;

public class ProcedureRequest {

    private String orderNo;
    private String machineNo;
    private Integer procedureNo;
    private LocalDateTime date;

    public String getOrderNo() {
        return orderNo;
    }

    public void setOrderNo(String orderNo) {
        this.orderNo = orderNo;
    }

    public String getMachineNo() {
        return machineNo;
    }

    public void setMachineNo(String machineNo) {
        this.machineNo = machineNo;
    }

    public Integer getProcedureNo() {
        return procedureNo;
    }

    public void setProcedureNo(Integer procedureNo) {

        this.procedureNo = procedureNo;
    }

    public LocalDateTime getDate() {
        return date;
    }

    public void setDate(LocalDateTime date) {
        this.date = date;
    }
}
