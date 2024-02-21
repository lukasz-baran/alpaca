
# Build

How to build Windows installer?
 
    mvn install -Pbuild-windows


## Requirements

Building of installation package requires:
* `jlink` - from JDK (you need to add it to your PATH)

###  How to build exe?

Download WiX tools https://wixtoolset.org

### Modules usage justification

* `javafx.web` - it is used to display About app dialog window (as it uses WebView JavaFX control)
* `jdk.crypto.cryptoki` - needed because of REST API calls to NBP (Poland National Bank) services - without this module you would get `javax.net.ssl.SSLHandshakeException: Received fatal alert: handshake_failure` exception when trying to display bank account details

### jconsole parameters

* `--win-console` - disables cmd console while running the application - expected by business, super-useful for the developers   


# Links, inspiration, etc

* Better input text fields: https://coderscratchpad.com/clearable-textfield-in-javafx-using-controlsfx/