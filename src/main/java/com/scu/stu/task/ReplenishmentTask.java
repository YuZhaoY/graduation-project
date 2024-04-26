package com.scu.stu.task;


import com.scu.stu.service.ReplenishmentService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;


@Component
public class ReplenishmentTask {

    @Autowired
    ReplenishmentService replenishmentService;

    @Scheduled(cron = "0 0 1 * * ?")
    public void execute() {
        System.out.println("start Schedule");
        replenishmentService.updateInvalid();
        replenishmentService.updateValid();
    }
}
