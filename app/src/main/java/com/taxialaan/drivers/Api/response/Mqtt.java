package com.taxialaan.drivers.Api.response;import com.google.gson.annotations.SerializedName;public class Mqtt{    @SerializedName("server")    private String server;    @SerializedName("qos")    private String qos;    @SerializedName("port")    private String port;    @SerializedName("env")    private String env;    public void setServer(String server){        this.server = server;    }    public String getServer(){        return server;    }    public void setQos(String qos){        this.qos = qos;    }    public String getQos(){        return qos;    }    public void setPort(String port){        this.port = port;    }    public String getPort(){        return port;    }    public void setEnv(String env){        this.env = env;    }    public String getEnv(){        return env;    }    @Override    public String toString(){        return                "Mqtt{" +                        "server = '" + server + '\'' +                        ",qos = '" + qos + '\'' +                        ",port = '" + port + '\'' +                        ",env = '" + env + '\'' +                        "}";    }}