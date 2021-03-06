package io.ryos.rhino.sdk.monitoring;

/**
 * Grafana dashboard representation. The dashboards will be created by using JSON templates while
 * starting the application, if Grafana integration is enabled.
 * <p>
 *
 * @author Erhan Bagdemir
 */
public interface GrafanaDashboard {

  /**
   * Returns the string representation of the dashboard.
   * <p>
   *
   * @param simulationName    The name of the simulation.
   * @param dashboardTemplate Dashboard template.
   * @param dsls              A list of dsls used in dashboards.
   * @return String representation of the dashboard.
   */
  String getDashboard(String simulationName,
      String dashboardTemplate,
      String[] dsls);
}
