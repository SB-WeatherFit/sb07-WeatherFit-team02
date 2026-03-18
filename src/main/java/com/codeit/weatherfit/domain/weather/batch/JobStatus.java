package com.codeit.weatherfit.domain.weather.batch;

public enum JobStatus {

    WEATHER_UPDATE("weatherUpdateJob","weatherUpdateStep");
    private final String jobName;
    private final String stepName;

    JobStatus(String jobName, String stepName) {
        this.jobName = jobName;
        this.stepName = stepName;
    }
    public String getJobName() {
        return jobName;
    }
    public String getStepName() {
        return stepName;
    }
}
