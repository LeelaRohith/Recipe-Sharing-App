import PrimarySearchAppBar from "./Toolbar";
import InputAdornment from "@mui/material/InputAdornment";
import TextField from "@mui/material/TextField";
import SearchIcon from "@mui/icons-material/Search";
import RecipeReviewCard from "./Recipecard";
import { useState, useEffect, useCallback } from "react";
import axios from "axios";
import { TailSpin } from "react-loader-spinner";
export default function Homepage() {
  const [recipedetails, setRecipedetails] = useState([]);
  const [filteredrecipes, setFilteredrecipes] = useState([]);
  const [userfirstname, setUserfirstname] = useState("");
  const [userlastname, setUserlastname] = useState("");

  //const [loading, setLoading] = useState(true);
  const handleSearch = (e) => {
    const searchQuery = e.target.value.toLowerCase();

    const filteredRecipes = recipedetails.filter((recipe) =>
      recipe.name.toLowerCase().includes(searchQuery)
    );
    setFilteredrecipes(filteredRecipes);
  };
  const fetchData = useCallback(async () => {
    const headers = {
      Authorization: "Bearer " + localStorage.getItem("token"),
    };
    const res = await axios.get(
      `${process.env.React_App_Backend_Url}/api/v1/user`,
      {
        headers,
      }
    );

    setUserfirstname(res.data.firstname);
    setUserlastname(res.data.lastname);

    const recipeResponse = await axios.get(
      `${process.env.React_App_Backend_Url}/api/v1/user/allrecipes`,
      {
        headers,
      }
    );

    setRecipedetails(recipeResponse.data);
  }, []);

  useEffect(() => {
    fetchData();
  }, [fetchData]);
  return (
    <div>
      <PrimarySearchAppBar></PrimarySearchAppBar>
      <br></br>
      <div style={{ textAlign: "center" }}>
        <TextField
          id="input-with-icon-textfield"
          label="Search Recipes"
          onChange={handleSearch}
          InputProps={{
            startAdornment: (
              <InputAdornment position="start">
                <SearchIcon />
              </InputAdornment>
            ),
          }}
          variant="outlined"
        />
      </div>
      <br></br>
      {recipedetails.length !== 0 ? null : (
        <div
          style={{
            display: "flex",
            flexDirection: "column",
            justifyContent: "center",
            alignItems: "center",
          }}
        >
          <TailSpin
            visible={true}
            height="30"
            width="30"
            color="#1976D2"
            ariaLabel="tail-spin-loading"
            radius="1"
            wrapperStyle={{}}
            wrapperClass=""
          />
          <div>Loading Recipes</div>
        </div>
      )}
      <div
        style={{
          display: "flex",
          flexDirection: "row",
          alignItems: "center",
          justifyContent: "center",
          flexWrap: "wrap",
          gap: "20px",
        }}
      >
        {filteredrecipes.length > 0
          ? filteredrecipes.map((item, index) => {
              return (
                <div key={index}>
                  <RecipeReviewCard
                    details={item}
                    userfirstname={userfirstname}
                    userlastname={userlastname}
                    image={item.image}
                    type="view"
                    userid={item.currentuserid}
                  ></RecipeReviewCard>
                </div>
              );
            })
          : recipedetails.map((item, index) => {
              return (
                <div key={index}>
                  <RecipeReviewCard
                    details={item}
                    userfirstname={item.userfirstname}
                    userlastname={item.userlastname}
                    image={item.image}
                    type="view"
                    userid={item.currentuserid}
                  ></RecipeReviewCard>
                </div>
              );
            })}
        {/* {filteredrecipes.length === 0 ? <div>No Results</div> : null} */}
        {/* {filteredrecipes.length === 0 &&
          recipedetails.map((item, index) => {
            return (
              <div key={index}>
                <RecipeReviewCard
                  details={item}
                  userfirstname={userfirstname}
                  userlastname={userlastname}
                  image={imageData[item.image]}
                  type="view"
                  userid={item.currentuserid}
                ></RecipeReviewCard>
              </div>
            );
          })} */}
      </div>
    </div>
  );
}
