// Copyright (c) 2013, Webit Team. All Rights Reserved.

package webit.script.core.ast.expressions;

import java.util.ArrayList;
import java.util.List;
import webit.script.core.ast.Expression;
import webit.script.core.ast.StatmentPart;

/**
 *
 * @author Zqq
 */
public final class MapValuePart extends StatmentPart{
    
    protected final List keys;
    protected final List<Expression> valueExprs;

    public MapValuePart(int line, int column) {
        super(line, column);
        this.keys = new ArrayList();
        this.valueExprs = new ArrayList();
    }
    
    public MapValuePart append(Object key, Expression expr){
        keys.add(key);
        valueExprs.add(expr);
        return this;
    }
    
    public MapValue pop(){

        int len = keys.size();

        Object[] _keys = new Object[len];
        Expression[] _valueExprs = new Expression[len];
        
        _keys = keys.toArray(_keys);
        _valueExprs = valueExprs.toArray(_valueExprs);
        
        return new MapValue(_keys, _valueExprs, line, column);
    }

}
