/*
 * Copyright (C) 2015 hcadavid
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */
package edu.eci.pdsw.webappsintro.jdbc.example.basic;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author hcadavid
 */
public class JDBCExample {
    
    public static void main(String args[]){
        try {
            String url="jdbc:mysql://desarrollo.is.escuelaing.edu.co:3306/bdprueba";
            String driver="com.mysql.jdbc.Driver";
            String user="bdprueba";
            String pwd="bdprueba";
                        
            Class.forName(driver);
            Connection con=DriverManager.getConnection(url,user,pwd);
            con.setAutoCommit(false);
                 
            
            System.out.println("Valor total pedido 1:"+valorTotalPedido(con, 1));
            
            List<String> prodsPedido=nombresProductosPedido(con, 1);
            
            
            System.out.println("Productos del pedido 1:");
            System.out.println("-----------------------");
            for (String nomprod:prodsPedido){
                System.out.println(nomprod);
            }
            System.out.println("-----------------------");
            
            
            int suCodigoECI=2106457;
            registrarNuevoProducto(con, suCodigoECI, "HeadPhones", 99999999);            
            con.commit();
            
            cambiarNombreProducto(con, suCodigoECI, "HeadPhones Ultra");
            con.commit();
            
            
            con.close();
                                   
        } catch (ClassNotFoundException | SQLException ex) {
            Logger.getLogger(JDBCExample.class.getName()).log(Level.SEVERE, null, ex);
        }
        
        
    }
    
    /**
     * Agregar un nuevo producto con los parámetros dados
     * @param con la conexión JDBC
     * @param codigo
     * @param nombre
     * @param precio
     * @throws SQLException 
     */
    public static void registrarNuevoProducto(Connection con, int codigo, String nombre,int precio) throws SQLException{
        //Crear prepared statement
        PreparedStatement ps = con.prepareStatement("INSERT INTO ORD_PRODUCTOS VALUES( ?, ?, ? )");
        //Asignar parámetros
        ps.setInt(1, codigo);
        ps.setString(2, nombre);
        ps.setInt(3, precio);
        ps.execute();
        //usar 'execute'

        
        con.commit();
        
    }
    
    /**
     * Consultar los nombres de los productos asociados a un pedido
     * @param con la conexión JDBC
     * @param codigoPedido el código del pedido
     * @return 
     * @throws SQLException 
     */
    public static List<String> nombresProductosPedido(Connection con, int codigoPedido) throws SQLException{
        List<String> np=new LinkedList<>();
        
        //Crear prepared statement
        PreparedStatement ps = con.prepareStatement("SELECT nombre FROM ORD_DETALLES_PEDIDO, ORD_PRODUCTOS WHERE pedido_fk = ? "
                + "AND producto_fk = codigo");
        //asignar parámetros
        ps.setInt(1, codigoPedido);
        //usar executeQuery
        ResultSet rs = ps.executeQuery();
        //Sacar resultados del ResultSet
        while(rs.next()){
            np.add(rs.getString(1));
        }
        //Llenar la lista y retornarla
        
        return np;
    }

    
    /**
     * Calcular el costo total de un pedido
     * @param con
     * @param codigoPedido código del pedido cuyo total se calculará
     * @return el costo total del pedido (suma de: cantidades*precios)
     * @throws SQLException 
     */
    public static int valorTotalPedido(Connection con, int codigoPedido) throws SQLException{
        
        //Crear prepared statement
        PreparedStatement ps = con.prepareStatement("SELECT SUM(precio*cantidad) FROM ORD_DETALLES_PEDIDO, ORD_PRODUCTOS WHERE pedido_fk = ? "
                + "AND producto_fk = codigo");
        //asignar parámetros
        ps.setInt(1, codigoPedido);
        //usar executeQuery
        ResultSet rs = ps.executeQuery();
        //Sacar resultado del ResultSet
        rs.next();
        return rs.getInt(1);
    }
    

    /**
     * Cambiar el nombre de un producto
     * @param con
     * @param codigoProducto codigo del producto cuyo nombre se cambiará
     * @param nuevoNombre el nuevo nombre a ser asignado
     * @throws SQLException 
     */
    public static void cambiarNombreProducto(Connection con, int codigoProducto, 
            String nuevoNombre) throws SQLException{
        
        //Crear prepared statement
        PreparedStatement ps = con.prepareStatement("UPDATE ORD_PRODUCTOS set nombre = ? where codigo = ?");
        //asignar parámetros
        ps.setString(1, nuevoNombre);
        ps.setInt(2, codigoProducto);
        //usar executeUpdate
        int rs = ps.executeUpdate();
        //verificar que se haya actualizado exactamente un registro
        if (rs != 1){
            throw new SQLException("No se actualiza solo un registro.");
        }
        
    }
    
    
    
}
