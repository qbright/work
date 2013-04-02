/*******************************************************************************
 *              Copyright (C) Bester Consulting 2005. All Rights reserved.
 * @author      John Bester
 * Project:     Library
 * Description: Library classes
 *
 * Changelog  
 *  $Log$
 *  Created on 05 Jun 2010
 *******************************************************************************/
package za.co.softco.bean;

import java.util.Set;

public interface BeanFilter {

  public Set<String> getPropertyFilter();
  
}
