package croscoretest;

import akka.actor.ActorRef;
import akka.actor.ActorSystem;
import akka.actor.Props;
import akka.dispatch.OnSuccess;
import droneapi.api.DroneCommander;
import droneapi.messages.FlyingStateChangedMessage;
import droneapi.messages.LocationChangedMessage;
import droneapi.model.properties.FlipType;
import java.io.IOException;
import parrot.ardrone2.ArDrone2Driver;
import parrot.ardrone3.BebopDriver;
import scala.concurrent.Future;

import static java.lang.Thread.sleep;
import java.util.logging.Level;

public class ArDrone2Example {

    public ArDrone2Example() {
        BebopDriver driver = new BebopDriver();
        ArDrone2Driver ar2Driver = new ArDrone2Driver();
        final DroneCommander commander = new DroneCommander("192.168.1.1", ar2Driver);

        ActorSystem system = ActorSystem.create();

        ActorRef logger = system.actorOf(Props.create(Logger.class));

        commander.subscribeTopics(logger, new Class[]{FlyingStateChangedMessage.class, LocationChangedMessage.class});
        Future<Void> init = commander.init();
        init.onSuccess(new OnSuccess<Void>() {
            @Override
            public void onSuccess(Void result) throws Throwable {
                commander.takeOff();
                sleep(5000);
                commander.flip(FlipType.FRONT);
                sleep(5000);
                commander.land();
            }
        }, system.dispatcher());

        try {
            System.in.read();
        } catch (IOException ex) {
            java.util.logging.Logger.getLogger(ArDrone2Example.class.getName()).log(Level.SEVERE, null, ex);
        }
        commander.land();
    }
}
