@Grab('com.github.igor-suhorukov:camel-gcode:0.1')
@Grab('org.apache.camel:camel-groovy:2.18.0')
@Grab('org.apache.camel:camel-core:2.18.0')
@Grab('org.apache.camel:camel-jetty:2.18.0')
@Grab('org.slf4j:slf4j-simple:1.6.6')
import org.apache.camel.builder.RouteBuilder
import org.apache.camel.impl.DefaultCamelContext
import com.github.igorsuhorukov.smreed.dropship.MavenClassLoader

def camelContext = new DefaultCamelContext()
camelContext.setName("I'll be back")
camelContext.addRoutes(new RouteBuilder() {
    def void configure() {
        from('jetty:http://0.0.0.0:9090/moveTo').routeId('CamelCNC')
                .process{ it.in.body =
                  ("set mdi g0 x${Math.round(Math.random()*10)} y${Math.round(Math.random()*10)} z1") }
                .to('gcode:?host=beaglebone.local&port=5007&autoHomeAxisCount=4')
    }
})
addShutdownHook{ camelContext.stop() }
camelContext.start()

def HawtIo = MavenClassLoader.usingCentralRepo()
        .forMavenCoordinates('io.hawt:hawtio-app:2.0.0').loadClass('io.hawt.app.App')
Thread.currentThread().setContextClassLoader(HawtIo.getClassLoader())
HawtIo.main('--port','10090')
