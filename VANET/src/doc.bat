cls
javadoc -private -classpath .;.\Simulation -d doc\private *.java
javadoc -classpath .;.\Simulation; -d doc\public *.java