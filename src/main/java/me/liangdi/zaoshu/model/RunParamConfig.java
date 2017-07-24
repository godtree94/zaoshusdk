/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package me.liangdi.zaoshu.model;

import java.util.LinkedList;
import java.util.List;

/**
 * @date Jun 30, 2017
 * @author ghh
 */
public class RunParamConfig {
    private List<RunConfig> target_sources=new LinkedList<>();

    public List<RunConfig> getTarget_sources() {
        return target_sources;
    }

    public void setTarget_sources(List<RunConfig> target_sources) {
        this.target_sources = target_sources;
    }
    
}
