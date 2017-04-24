package com.gat.repository.entity;

/**
 * Created by Rey on 2/13/2017.
 */

public interface Id {

    Id NONE = new NoneId();

    class NoneId implements Id{

        private NoneId(){}

        @Override
        public boolean equals(Object obj) {
            return obj instanceof NoneId;
        }

    }

}