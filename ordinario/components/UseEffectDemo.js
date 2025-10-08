import React, {use, useEffect, useState} from "react";
import { StyleSheet, Text } from "react-native";

export default ()=>{
    const [loadig, setLoadig]=useState(true)

        useEffect(()=>{
            setTimeout(()=>{
                setLoadig(false)
            },2000)
            console.log("Ejecutando Effect...")
        },[loadig])

        return(
        <>
            <Text style={styles.texto}>
                {loadig ? "Cargando...":"Listo!!!"}
                setLoading(true)
            </Text>
    
        </>
    )
}

const styles= StyleSheet.create({
    texto:{
        fontSize:48,
    }

})