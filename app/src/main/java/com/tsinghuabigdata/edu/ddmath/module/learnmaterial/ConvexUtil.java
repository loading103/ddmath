package com.tsinghuabigdata.edu.ddmath.module.learnmaterial;

import android.graphics.Point;

import java.util.ArrayList;

/**
 *凸多边形的判断
 */

public class ConvexUtil {

    private final static double eps = 1e-10;

    private int dcmp(double x){
        if( Math.abs(x) < eps) return 0;
        return x < 0 ? -1:1;
    }

    private class Vector extends android.graphics.PointF{

        /*public*/ Vector( double x, double y){
            this.x = (float) x;
            this.y = (float) y;
        }

        boolean lessthan( Vector p ){
            return x < p.x || (x == p.x && y < p.y);
        }

        @Override
        public boolean equals(Object o) {
            if( o instanceof Vector ){
                Vector p = (Vector)o;
                return dcmp(x - p.x) == 0&& dcmp(y - p.y) == 0;
            }
            return false;
        }
    }

    // 求向量 (x,y)的极角，atan2(y,x); -- C标准库
    Vector add(Vector A,Vector B){
        return new Vector( A.x + B.x , A.y + B.y );
    }
    Vector sub(Vector A,Vector B){
        return new Vector(A.x - B.x , A.y - B.y);
    }
    Vector mul(Vector A,double p){
        return new Vector(A.x*p , A.y*p);
    }
    Vector div(Vector A,double p){
        return new Vector(A.x/p , A.y/p);
    }

    //点积
    private double Dot(Vector A,Vector B) {return A.x*B.x+A.y*B.y;}
    //叉积
    private double Cross(Vector A,Vector B) {return A.x*B.y-A.y*B.x;}
    //求向量长度
    private double Length(Vector A){  return Math.sqrt(Dot(A,A));}
    //求出cos ，再用acos求出角度、
    private double Angle(Vector A,Vector B){ return Math.acos(Dot(A,B) / Length(A) / Length(B)); }

    double Area2(Vector A,Vector B,Vector C){ return Cross( sub(B,A),sub(C,A) );}

    //向量旋转 公式 x' = x * cosa - y * sina, y' = x * sina + y * cosa;
    Vector Rotate(Vector A,double rad){
        return new Vector( A.x * Math.cos(rad) - A.y*Math.sin(rad), A.x*Math.sin(rad) + A.y * Math.cos(rad) );
    }

    //计算向量的 单位 法线。
    Vector Normal(Vector A){double L = Length(A); return new Vector(-A.y / L , A.x /L); }

    //计算交点， 调用前确保 Cross(v,w) 非0
//设 直线分别为 P + tv 和 Q + tw;
    
    Vector GetLineIntersection(Vector P,Vector v,Vector Q,Vector w){
        Vector u = sub(P,Q);
        double t = Cross( w , u) / Cross( v , w );
        return add(P,mul(v,t));
    }
    //点到直线距离
    double DistanceToLine(Vector P, Vector A,Vector B){ //叉积 除以 底
        Vector v1 = sub(B, A), v2 = sub(P, A);
        return Math.abs(Cross(v1,v2)) / Length(v1); //不取绝对值，则为有向距离
    }
    //点到线段的距离
    double DistanceToSegment(Vector P, Vector A, Vector B){
        if(A == B) return Length( sub(P, A) ) ; // AB重合，成点对点长度
        Vector v1 = sub(B, A), v2 = sub(P, A), v3 = sub(P,B);
        if(dcmp(Dot(v1,v2)) < 0) return Length(v2); // == 0 的时候是垂直的，小于零在二三象限； 即离A近；
        else if(dcmp(Dot(v1,v2)) > 0) return Length(v3);  //大于零 一四象限。
        else return Math.abs(Cross(v1,v2)) / Length(v1); // 垂直的情况，直接用叉积来求了。
    }
    Vector GetLineProjection(Vector P,Vector A,Vector B){ //获得P在线段AB上投影的节点。
        // AB向量  A + tv , Q 即投影点 A + t0v ,
        // PQ 垂直于AB ，Dot()应该为0. 所以 Dot(v , P - (A + t0v))'
        // 分配率  Dot(v , P - A) - t0 * Dot(v,v) = 0;
        Vector v = sub(B, A);
        return sub(A, mul(v, Dot(v , sub(P, A))/Dot(v,v)));
    }
    //判断线段相交
// 规范相交 ： 两线段恰好有一个公共点。且不在端点。
// 充要条件： 每条线段两个端点都在另一条线段的两侧。(叉积符号不同)
    boolean SegmentProperIntersection(Vector a1, Vector a2,Vector b1,Vector b2){
        double c1 = Cross( sub(a2,a1) , sub(b1, a1)) , c2 = Cross( sub(a2,a1) , sub(b2, a1) ) ;
        double c3 = Cross( sub(b2,b1) , sub(a1, b1)) , c4 = Cross( sub(b2,b1) , sub(a2, b1)) ;
        return dcmp(c1) * dcmp(c2) < 0 && dcmp(c3) * dcmp(c4) < 0 ;
    }
//判断一个点是否在线段上（不包括端点）

    boolean OnSegment(Vector p , Vector a1, Vector a2){   //不算端点
        //前一个判断是否共线，如果共线且点积相反，说明在线段上
        return dcmp(Cross( sub(a1, p) , sub(a2, p))) == 0 && dcmp(Dot( sub(a1, p), sub(a2, p))) < 0 ;
    }


    Vector getD(Vector A, Vector B, Vector C){ // A B C 逆时针给出
        Vector v1 = sub(C, B);
        double a1 = Angle( sub(A, B) , v1);
        v1 = Rotate(v1, a1 / 3);

        Vector v2 = sub(B, C);
        double a2 = Angle( sub(A,C),v2);
        v2 = Rotate(v2 , -a2 / 3);

        return  GetLineIntersection(B,v1,C,v2);
    }



    public boolean judgeConvex(ArrayList<Point> list){
        if( list==null || list.size() == 0 ) return false;

        int n = list.size();
        Vector po[] = new Vector[10];
        for(int i=0;i<n;i++){
            Point point = list.get(i);
            po[i] = new Vector( point.x, point.y );
        }

        int i;
        po[n] = po[0]; po[n+1] = po[1]; //将开始两个节点放到后面，方便循环遍历。
        for(i=1;i<n+1;i++){
            double tmp = Cross( sub(po[i-1],po[i] ), sub(po[i+1], po[i]));
            if(tmp < 0) break;
        }

        return i == n+1 && n >2;
    }

}
